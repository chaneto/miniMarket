package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.views.CartViewModel;
import com.example.minimarket.repositories.CartRepository;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final OrderService orderService;
    private final ModelMapper mapper;

    public CartServiceImpl(CartRepository cartRepository, OrderService orderService, ModelMapper mapper) {
        this.cartRepository = cartRepository;
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @Override
    public List<CartEntity> findByCourierIsNotNull() {
        return this.cartRepository.findByCourierIsNotNull();
    }

    @Override
    public boolean cartWithCourierWithUndeliveredOrder(String courierName){
        boolean result = false;
        for(CartEntity cart : findByCourierIsNotNull()){
            if(cart.getCourier().getName().equals(courierName)){
                result = true;
                break;
            }
        }return result;
    }

    @Override
    public List<CartEntity> findByAddressIsNotNull() {
        return this.cartRepository.findByAddressIsNotNull();
    }

    @Override
    public boolean cartWithOrderNotDeliveredToAddress(AddressEntity address){
        boolean result = false;
        for(CartEntity cart : findByAddressIsNotNull()){
            if(cart.getAddress().getId().equals(address.getId())){
                result = true;
                break;
            }
        }return result;
    }

    public CartEntity createCart(UserEntity userEntity){
        CartEntity cartEntity = new CartEntity();
        cartEntity.setTotalPrice(BigDecimal.valueOf(0));
        cartEntity.setUser(userEntity);
        this.cartRepository.save(cartEntity);
        return cartEntity;
    }

    @Override
    public void setTotalPrice(BigDecimal totalPrice, Long id) {
        this.cartRepository.setTotalPrice(totalPrice, id);
    }

    @Override
    public void updateTotalPrice(CartEntity cart) {
        setTotalPrice(cart.getTotalPrice().add(cart.getCourier().getShippingAmount()), cart.getId());
    }

    @Override
    public CartViewModel getCartById(Long id){
        CartViewModel cartViewModel = null;
        CartEntity cartEntity = this.cartRepository.getCartById(id);
        if(cartEntity != null){
            cartViewModel = this.mapper.map(cartEntity, CartViewModel.class);
            cartViewModel.setUser(cartEntity.getUser().getUsername());
        }
        return cartViewModel;
    }

    @Override
    public void setCourier(CourierEntity courierEntity, Long id) {
        this.cartRepository.setCourier(courierEntity, id);
    }

    @Override
    public void setAddress(AddressEntity addressEntity, Long cartId) {
        this.cartRepository.setAddress(addressEntity, cartId);
        this.orderService.setAddressAndCourier(this.mapper.map(this.cartRepository.getCartById(cartId), CartServiceModel.class));
    }

    @Override
    public void addProductToCart(String productName, BigDecimal quantity, Long cartId) {
        CartEntity cartEntity = this.cartRepository.getCartById(cartId);
        OrderEntity orderEntity = this.orderService.createOrder(productName, quantity, cartEntity);
        setTotalPrice(cartEntity.getTotalPrice().add(orderEntity.getTotalPrice()), cartEntity.getId());
    }

    @Override
    public void resetCart(Long id) {
        this.cartRepository.setTotalPrice(BigDecimal.valueOf(0.0), id);
        this.cartRepository.setCourier(null, id);
        this.cartRepository.setAddress(null, id);
        this.orderService.updateOrderToOrdered(id);
    }

    @Override
    public void clearCartById(Long cartId) {
        this.orderService.deleteAllIsNotOrderedOrders(cartId);
                resetCart(cartId);
    }

    @Override
    public void deleteCartById(Long id){
        this.cartRepository.deleteById(id);
    }

}
