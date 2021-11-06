package com.example.minimarket.services;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.views.CartViewModel;
import com.example.minimarket.model.views.OrderViewModel;
import org.hibernate.criterion.Order;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    void createOrder(ProductEntity productEntity, UserEntity userEntity, BigDecimal quantity);

    List<OrderViewModel> findAllByCartId(Long id);

    void setAddressAndCourier(CartServiceModel cart);

    void deleteOrderById(Long id);

    OrderEntity findOrderById(Long id);

    void setIsPaid(Boolean isPaid, Long id);

    void updateOrder(Long id);

    void deleteAllCartOrders(Long id);

    List<OrderViewModel> findAllOrderByIsPaid(Boolean isPaid, Long id);
}

