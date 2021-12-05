package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.views.OrderViewModel;
import com.example.minimarket.repositories.OrderRepository;
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
        orderEntity.setTotalPrice(productEntity.getPromotionPrice().multiply(quantity));
        orderEntity.setPaid(false);
        orderEntity.setDelivered(false);
        orderEntity.setOrdered(false);
        orderEntity.setUsername(cartEntity.getUser().getUsername());
        orderEntity.setCart(cartEntity);
        this.orderRepository.save(orderEntity);
        return orderEntity;
    }

    @Override
    public void setAddressAndCourier(CartServiceModel cart) {
        for(OrderEntity order: this.orderRepository.findAllOrderByIsOrderedAndCartId(false, cart.getId())){
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
    public void deleteAllIsNotOrderedOrders(Long cartId){
        for(OrderEntity order: this.orderRepository.findAllOrderByIsOrderedAndCartId(false, cartId)){
            deleteOrderById(order.getId());
        }
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
        for(OrderEntity order: this.orderRepository.findAllOrderByIsPaidAndCartId(false,id)){
            this.orderRepository.setIsPaid(true, order.getId());
            }
    }

    @Override
    public void setIsOrdered(Boolean isOrdered, Long id) {
        this.orderRepository.setIsOrdered(isOrdered, id);
    }

    @Override
    public void updateOrderToOrdered(Long id) {
        for(OrderEntity order: this.orderRepository.findAllOrderByIsOrderedAndCartId(false,id)){
            this.orderRepository.setIsOrdered(true, order.getId());
        }
    }

    @Override
    public List<OrderViewModel> findAllByAddressId(Long id) {
        return conversionToListViewModel(this.orderRepository.findAllByAddressId(id));
    }

    @Override
    public void setOrdersToDelivered(Long id) {
        for(OrderEntity order : this.orderRepository.findAllByAddressId(id)){
            setIsDelivered(true, order.getId());
        }
    }

    @Override
    public void setOrdersToPaid(Long id) {
        for(OrderEntity order: this.orderRepository.findAllByAddressId(id)){
            setIsPaid( true, order.getId());
        }
    }

    @Override
    public List<OrderViewModel> findAllOrdersOrderByDateTime() {
        return conversionToListViewModel(this.orderRepository.findAllOrderByDateTime());
    }

    @Override
    public void setIsDelivered(Boolean isDelivered, Long id) {
        this.orderRepository.setIsDelivered(isDelivered, id);
    }

    @Override
    public List<OrderViewModel> findAllOrderByIsPaidAndCartId(Boolean isPaid, Long id) {
        List<OrderViewModel> orderViews = new ArrayList<>();
        for(OrderEntity order : this.orderRepository.findAllOrderByIsPaidAndCartId(false, id )){
            OrderViewModel orderViewModel = this.mapper.map(order, OrderViewModel.class);
            orderViews.add(orderViewModel);
        }
        return orderViews;
    }

    @Override
    public boolean productInUnpaidOrder(String name) {
        List<OrderEntity> orders = this.orderRepository.findAllByIsPaidAndProductName(false, name);
        if(orders.size() > 0){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unpaidProductInBrand(String brandName) {
        List<OrderEntity> orders = this.orderRepository.findAllByIsPaid(false);
        int count = 0;
        for(OrderEntity order: orders){
          if(order.getProduct().getBrand().getName().equals(brandName)){
              count++;
              break;
          }
        }
        if(count > 0){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unpaidProductInCategory(String categoryName) {
        List<OrderEntity> orders = this.orderRepository.findAllByIsPaid(false);
        int count = 0;
        for(OrderEntity order: orders){
            if(order.getProduct().getCategory().getName().equals(categoryName)){
                count++;
                break;
            }
        }
        if(count > 0){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unpaidProductInCourier(String courierName) {
        List<OrderEntity> orders = this.orderRepository.findAllByIsPaid(false);
        int count = 0;
        for(OrderEntity order: orders){
            if(order.getCourier() != null){
            if(order.getCourier().getName().equals(courierName)) {
                count++;
                break;
             }
           }
        }
        if(count > 0){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<OrderViewModel> findAllOrderByIsOrderedAndCartId(Boolean ordered, Long id) {
        return conversionToListViewModel(this.orderRepository.findAllOrderByIsOrderedAndCartId(ordered, id));
    }

    @Override
    public List<OrderViewModel> findAllByCartId(Long id) {
      return conversionToListViewModel(this.orderRepository.findAllByCartIdOrderByDateTimeDesc(id));
    }

    @Override
    public List<OrderViewModel> findAllByIsDeliveredOrderByDateTime(boolean isDelivered) {
        return conversionToListViewModel(this.orderRepository.findAllByIsDeliveredOrderByDateTimeDesc(isDelivered));
    }

    public List<OrderViewModel> conversionToListViewModel(List<OrderEntity> orders){
        List<OrderViewModel> ordersView = new ArrayList<>();
        for(OrderEntity order: orders){
            OrderViewModel orderViewModel = this.mapper.map(order, OrderViewModel.class);
            String date  = orderViewModel.getDateTime().substring(0,10) + " " + orderViewModel.getDateTime().substring(11, 19);
            orderViewModel.setDateTime(date);
            ordersView.add(orderViewModel);
        }
        return ordersView;
    }
}
