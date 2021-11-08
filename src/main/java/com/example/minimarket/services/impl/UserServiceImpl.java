package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.CartEntity;
import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.enums.UserRole;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.model.services.UserServiceModel;
import com.example.minimarket.model.views.CartViewModel;
import com.example.minimarket.model.views.OrderViewModel;
import com.example.minimarket.repositories.UserRepository;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserRoleService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, UserRoleService userRoleService, MarketUserService marketUserService, PasswordEncoder passwordEncoder, CartService cartService, OrderService orderService) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userRoleService = userRoleService;
        this.marketUserService = marketUserService;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
        this.orderService = orderService;
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
            userEntity.setRoles(this.userRoleService.findAll());
        }else {
            userEntity.setRoles(this.userRoleService.findAllByUserRole(UserRole.USER));
        }
        CartEntity cartEntity = this.cartService.createCart(userEntity);
        userEntity.setCart(cartEntity);
        this.userRepository.save(userEntity);
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
        return this.orderService.findAllOrderByIsPaid(false, getCartId()).size();
    }

    @Override
    public BigDecimal getTotalPriceForAllOrders(){
        return getCurrentUser().getCart().getTotalPrice();
    }

    @Override
    public Long getCartId(){
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
    public List<OrderViewModel> getAllUserOrderByIsPaid(Boolean isPaid, Long id){
       return this.orderService.findAllOrderByIsPaid(isPaid, id);
    }

}
