package com.example.minimarket.model.services;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.model.entities.OrderEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartServiceModel {

    private Long id;
    private List<OrderEntity> orders = new ArrayList<>();
    private String user;
    private BigDecimal totalPrice;
    private AddressEntity address;
    private CourierEntity courier;

    public CartServiceModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public CourierEntity getCourier() {
        return courier;
    }

    public void setCourier(CourierEntity courier) {
        this.courier = courier;
    }
}
