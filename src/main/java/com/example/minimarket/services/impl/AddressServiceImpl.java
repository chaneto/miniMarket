package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.services.AddressServiceModel;
import com.example.minimarket.repositories.AddressRepository;
import com.example.minimarket.services.AddressService;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapper mapper;
    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;

    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper mapper, UserService userService, CartService cartService, OrderService orderService) {
        this.addressRepository = addressRepository;
        this.mapper = mapper;
        this.userService = userService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @Override
    public void save(AddressServiceModel addressServiceModel) {
        AddressEntity addressEntity = this.mapper.map(addressServiceModel, AddressEntity.class);
        addressEntity.setUser(this.userService.getCurrentUser());
        this.addressRepository.save(addressEntity);
        this.cartService.setAddress(addressEntity, this.userService.getCurrentUser().getCart().getId());
        this.orderService.setAddressAndCourier(this.userService.getCurrentCart());
    }
}
