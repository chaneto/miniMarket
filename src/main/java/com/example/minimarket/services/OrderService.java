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
    OrderEntity createOrder(String productName, BigDecimal quantity, CartEntity cartEntity);

    void setAddressAndCourier(CartServiceModel cart);

    void deleteOrderById(Long id);

    OrderEntity findOrderById(Long id);

    void setIsPaid(Boolean isPaid, Long id);

    void updateOrderToPaid(Long id);

    void deleteAllIsNotOrderedOrders(Long cartId);

    List<OrderViewModel> findAllOrderByIsPaidAndCartId(Boolean isPaid, Long id);

    boolean productInUnpaidOrder(String name);

    boolean unpaidProductInBrand(String brandName);

    boolean unpaidProductInCategory(String categoryName);

    boolean unpaidProductInCourier(String courierName);

    List<OrderViewModel> findAllByCartId(Long id);

    List<OrderViewModel> findAllByIsDeliveredOrderByDateTime(boolean isDelivered);

    List<OrderViewModel> findAllOrderByIsOrderedAndCartId(Boolean IsOrdered, Long id);

    void setIsOrdered(Boolean isOrdered, Long id);

    void setIsDelivered(Boolean isDelivered, Long id);

    void updateOrderToOrdered(Long id);

    List<OrderViewModel> findAllByAddressId(Long id);

    void setOrdersToDelivered(Long id);

    void setOrdersToPaid(Long id);

    List<OrderViewModel> findAllOrdersOrderByDateTime();
}

