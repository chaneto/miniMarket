package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.views.OrderViewModel;
import com.example.minimarket.repositories.OrderRepository;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.ProductService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ModelMapper mapper;


    public OrderServiceImpl(OrderRepository orderRepository, ModelMapper mapper, ProductService productService) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.productService = productService;
    }

    @Override
    public OrderEntity createOrder(String productName, BigDecimal quantity, CartEntity cartEntity) {
        OrderEntity orderEntity = new OrderEntity();
        this.productService.buyProduct(productName, quantity);
        ProductEntity productEntity = this.productService.findByNameEntity(productName);
        orderEntity.setProduct(productEntity);
        orderEntity.setProductCount(quantity.intValue());
        orderEntity.setDateTime(LocalDateTime.now());
        orderEntity.setTotalPrice(productEntity.getPrice().multiply(quantity));
        orderEntity.setPaid(false);
        orderEntity.setCart( cartEntity);
        this.orderRepository.save(orderEntity);
        return orderEntity;
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
        OrderEntity order = this.orderRepository.findOrderById(id);
        this.productService.addQuantity(BigDecimal.valueOf(order.getProductCount()), order.getProduct().getName());
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
    public void updateOrderToPaid(Long id) {
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
    public void deleteAllIsNotPaidOrders(Long cartId){
        for(OrderEntity order: this.orderRepository.findAllOrderByIsPaid(false, cartId)){
            deleteOrderById(order.getId());
        }
    }
}
