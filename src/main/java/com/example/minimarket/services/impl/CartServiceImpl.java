package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.views.CartViewModel;
import com.example.minimarket.repositories.CartRepository;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ModelMapper mapper;

    public CartServiceImpl(CartRepository cartRepository, ModelMapper mapper) {
        this.cartRepository = cartRepository;
        this.mapper = mapper;
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
    public CartViewModel getCartById(Long id){
        CartViewModel cartViewModel = new CartViewModel();
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
    public void setAddress(AddressEntity addressEntity, Long id) {
        this.cartRepository.setAddress(addressEntity, id);
    }

    @Override
    public void updateTotalPrice(CartEntity cart) {
        this.cartRepository.setTotalPrice(cart.getTotalPrice().add(cart.getCourier().getShippingAmount()), cart.getId());
    }

    @Override
    public void updateCart(Long id) {
        this.cartRepository.setTotalPrice(BigDecimal.valueOf(0.0), id);
        this.cartRepository.setCourier(null, id);
        this.cartRepository.setAddress(null, id);
    }
}