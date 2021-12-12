package com.example.minimarket.services.impl;

import com.example.minimarket.model.bindings.UserRegisterBindingModel;
import com.example.minimarket.model.entities.CartEntity;
import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.entities.UserRoleEntity;
import com.example.minimarket.model.enums.UserRole;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.model.services.UserServiceModel;
import com.example.minimarket.model.views.OrderViewModel;
import com.example.minimarket.model.views.UserViewModel;
import com.example.minimarket.repositories.UserRepository;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserRoleService;
import com.example.minimarket.services.UserService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final UserRoleService userRoleService;
    private final MarketUserService marketUserService;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;
    private final OrderService orderService;
    private final Gson gson;
    private final Resource userFile;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, UserRoleService userRoleService, MarketUserService marketUserService
            , PasswordEncoder passwordEncoder, CartService cartService, OrderService orderService, Gson gson,@Value("classpath:init/users.json") Resource userFile) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userRoleService = userRoleService;
        this.marketUserService = marketUserService;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
        this.orderService = orderService;
        this.gson = gson;
        this.userFile = userFile;
    }

    @Override
    public UserRegisterServiceModel findByUsername(String username) {
     return  this.mapper.map(this.userRepository.findByUsername(username), UserRegisterServiceModel.class);
    }

    @Override
    public UserEntity findByUsernameEntity(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public UserRegisterServiceModel findByUsernameAndEmail(String username, String email) {
        return  this.mapper.map(this.userRepository.findByUsernameAndEmail(username, email), UserRegisterServiceModel.class);
    }

    @Override
    public List<UserEntity> findAllOrderByUsername() {
        return this.userRepository.findAllUsersOrderByUsername();
    }

    @Override
    public boolean userWithUsernameIsExists(String username) {
        UserEntity userEntity = this.userRepository.findByUsername(username);
        if(userEntity != null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean userWithEmailIsExists(String email) {
        UserEntity userEntity = this.userRepository.findByEmail(email);
        if(userEntity != null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void registerUser(UserRegisterServiceModel userRegisterServiceModel) {
        UserEntity userEntity = this.mapper.map(userRegisterServiceModel, UserEntity.class);
        userEntity.setPassword(this.passwordEncoder.encode(userEntity.getPassword()));
        if(this.userRepository.count() == 0){
            userEntity.setRole(this.userRoleService.findByUserRole(UserRole.ADMIN));
        }else {
            userEntity.setRole(this.userRoleService.findByUserRole(UserRole.USER));
        }
        CartEntity cartEntity = this.cartService.createCart(userEntity);
        userEntity.setCart(cartEntity);
        this.userRepository.save(userEntity);
    }

    public void seedUsersFromJson() throws IOException {
        if(this.userRepository.count() == 0){
        UserRegisterBindingModel[] users = this.gson.fromJson(Files.readString(Path.of(userFile.getURI())), UserRegisterBindingModel[].class);
        for(UserRegisterBindingModel user: users){
            if(!userWithEmailIsExists(user.getEmail()) && !userWithUsernameIsExists(user.getUsername())){
            registerUser(this.mapper.map(user, UserRegisterServiceModel.class));
               }
            }
        }
    }

    @Override
    public void authenticate(UserLoginServiceModel userLoginServiceModel) {
        UserDetails principal = this.marketUserService.loadUserByUsername(userLoginServiceModel.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, userLoginServiceModel.getPassword(), principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public int getCountAllUserOrders(){
        return this.orderService.findAllOrderByIsOrderedAndCartId(false, getCurrentCartId()).size();
    }

    @Override
    public BigDecimal getTotalPriceForAllOrders(){
        return getCurrentUser().getCart().getTotalPrice();
    }

    @Override
    public Long getCurrentCartId(){
        return getCurrentUser().getCart().getId();
    }

    @Override
    public UserServiceModel getCurrentUser(){
        UserServiceModel userServiceModel = new UserServiceModel();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        userServiceModel = this.mapper.map(this.userRepository.findByUsername(userDetails.getUsername()), UserServiceModel.class);
       return userServiceModel;
    }

    @Override
    public List<String> findAllUsername() {
        return this.userRepository.findAllUsername();
    }

    @Override
    public void setUserRole(String username, String roleName) {
        if(getCurrentUser().getRole().getUserRole().name().equals("ADMIN") && !username.equals("admin")){
        UserEntity userEntity = this.userRepository.findByUsername(username);
        UserRoleEntity userRoleEntity = this.userRoleService.findByUserRole(UserRole.valueOf(roleName));
        this.userRepository.setUserRole(userRoleEntity, userEntity.getId());
        }
    }

    @Override
    public void setUserPassword(String password, Long id) {
        this.userRepository.setUserPassword(password, id);
    }

    @Override
    public void changePassword(String password) {
        setUserPassword(this.passwordEncoder.encode(password), getCurrentUser().getId());
    }

    @Override
    public boolean passwordMatches(String password, String confirmPassword) {
       return password.equals(confirmPassword);
    }

    @Override
    public void changeEmail(String email) {

        setUserEmail(email, getCurrentUser().getId());
    }

    public void setUserEmail(String email, Long id) {
        this.userRepository.setUserEmail(email, id);
    }

    @Override
    public CartServiceModel getCurrentCart(){
        CartServiceModel cartServiceModel = new CartServiceModel();
        CartEntity cartEntity = getCurrentUser().getCart();
        if(cartEntity != null){
            cartServiceModel = this.mapper.map(cartEntity, CartServiceModel.class);
            cartServiceModel.setUser(cartEntity.getUser().getUsername());
        }
        return cartServiceModel;
    }

    @Override
    public List<OrderViewModel> getAllUserOrderByIsOrdered(Boolean isPaid, Long id){
       return this.orderService.findAllOrderByIsOrderedAndCartId(isPaid, id);
    }

    @Override
    public List<UserViewModel> getAllUsers(){
        List<UserViewModel> users = new ArrayList<>();
        for(UserEntity user: findAllOrderByUsername()){
            UserViewModel userViewModel = this.mapper.map(user, UserViewModel.class);
            userViewModel.setRole(user.getRole().getUserRole().name());
            users.add(userViewModel);
        }
        return users;
    }

    @Override
    public void deleteUserByUsername(String username){
        if(getCurrentUser().getRole().getUserRole().name().equals("ADMIN") && !getCurrentUser().getUsername().equals(username)){
        CartEntity cart = this.userRepository.findByUsername(username).getCart();
        if(cart.getAddress() != null){
            this.cartService.setAddress(null, cart.getId());
        }
        this.cartService.deleteCartById(cart.getId());
        }
    }

}
