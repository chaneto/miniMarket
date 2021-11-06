package com.example.minimarket.services;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.entities.CartEntity;
import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.views.CartViewModel;
import com.example.minimarket.model.views.OrderViewModel;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    CartEntity createCart(UserEntity userEntity);

   void setTotalPrice(BigDecimal totalPrice, Long id);

    CartViewModel getCartById(Long id);

    void setCourier(CourierEntity courierEntity, Long id);

    void setAddress(AddressEntity addressEntity, Long id);

    void updateTotalPrice(CartEntity cart);

    void updateCart(Long id);

}
