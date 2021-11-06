package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.views.OrderViewModel;
import com.example.minimarket.repositories.OrderRepository;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;


    public OrderServiceImpl(OrderRepository orderRepository, ModelMapper mapper, CartService cartService) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.cartService = cartService;
    }

    @Override
    public void createOrder(ProductEntity productEntity, UserEntity userEntity, BigDecimal quantity) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setProduct(productEntity);
        orderEntity.setProductCount(quantity.intValue());
        orderEntity.setDateTime(LocalDateTime.now());
        orderEntity.setTotalPrice(productEntity.getPrice().multiply(quantity));
        orderEntity.setCart(userEntity.getCart());
        orderEntity.setPaid(false);
        this.orderRepository.save(orderEntity);
        this.cartService.setTotalPrice(userEntity.getCart().getTotalPrice().add(orderEntity.getTotalPrice()), userEntity.getCart().getId());
    }

    @Override
    public List<OrderViewModel> findAllByCartId(Long id) {
        List<OrderViewModel> orderViews = new ArrayList<>();
        for(OrderEntity order : this.orderRepository.findAllOrderByIsPaid(false, id )){
            OrderViewModel orderViewModel = this.mapper.map(order, OrderViewModel.class);
            orderViewModel.setProduct(order.getProduct().getName());
            orderViews.add(orderViewModel);
        }
        return orderViews;
    }

    @Override
    public void setAddressAndCourier(CartServiceModel cart) {
        for(OrderEntity order: cart.getOrders()){
            this.orderRepository.setCourier(cart.getCourier(), order.getId());
            this.orderRepository.setAddress(cart.getAddress(), order.getId());
        }
    }

    @Override
    public void deleteOrderById(Long id){
        this.orderRepository.deleteOrderById(id);
    }

    @Override
    public OrderEntity findOrderById(Long id) {
        return this.orderRepository.findOrderById(id);
    }

    @Override
    public void setIsPaid(Boolean isPaid, Long id) {
        this.orderRepository.setIsPaid(isPaid, id);
    }

    @Override
    public void updateOrder(Long id) {
        for(OrderEntity order: this.orderRepository.findAllOrderByIsPaid(false,id)){
            this.orderRepository.setIsPaid(true, order.getId());
            }
    }

    @Override
    public List<OrderViewModel> findAllOrderByIsPaid(Boolean isPaid, Long id) {
        List<OrderViewModel> orderViews = new ArrayList<>();
        for(OrderEntity order : this.orderRepository.findAllOrderByIsPaid(false, id )){
            OrderViewModel orderViewModel = this.mapper.map(order, OrderViewModel.class);
            orderViewModel.setProduct(order.getProduct().getName());
            orderViews.add(orderViewModel);
        }
        return orderViews;
    }

    @Override
    public void deleteAllCartOrders(Long id){
        for(OrderEntity order: this.cartService.getCartById(id).getOrders()){
            deleteOrderById(order.getId());
        }
    }
}
