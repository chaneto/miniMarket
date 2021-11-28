package com.example.minimarket.services.impl;

import com.example.minimarket.model.bindings.CourierAddBindingModel;
import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.model.services.CourierServiceModel;
import com.example.minimarket.model.views.CourierViewModel;
import com.example.minimarket.repositories.CourierRepository;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.CourierService;
import com.example.minimarket.services.UserService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourierServiceImpl implements CourierService {

    private final CourierRepository courierRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final Resource courierFile;
    private final UserService userService;
    private final CartService cartService;

    public CourierServiceImpl(@Value("classpath:init/couriers.json") Resource courierFile, CourierRepository courierRepository, ModelMapper mapper, Gson gson, UserService userService, CartService cartService) {
        this.courierRepository = courierRepository;
        this.mapper = mapper;
        this.gson = gson;
        this.courierFile = courierFile;
        this.userService = userService;
        this.cartService = cartService;
    }

    @Override
    public CourierServiceModel findByName(String name) {
        if(this.courierRepository.findByName(name) != null){
        return this.mapper.map(courierRepository.findByName(name), CourierServiceModel.class);
        }else {
            return null;
        }
    }

    @Override
    public void saveCourier(CourierServiceModel courierServiceModel) {
        this.courierRepository.save(this.mapper.map(courierServiceModel, CourierEntity.class));
    }

    @Override
    public List<CourierViewModel> findAll() {
        return this.courierRepository.findAll()
                .stream()
                .map(m -> this.mapper.map(m, CourierViewModel.class)).collect(Collectors.toList());
    }

    @Override
    public void seedCourierFromJson() throws IOException {
        if(this.courierRepository.count() == 0){
            CourierAddBindingModel[] couriers = this.gson.fromJson(Files.readString(Path.of(courierFile.getURI())), CourierAddBindingModel[].class);
            for(CourierAddBindingModel courier: couriers){
                if(findByName(courier.getName()) == null){
                CourierEntity courierEntity = this.mapper.map(courier, CourierEntity.class);
                this.courierRepository.save(courierEntity);
                }
            }
        }
    }

    @Override
    public void setCourierCart(String name) {
        CourierEntity courierEntity = this.courierRepository.findByName(name);
       this.cartService.setCourier(courierEntity, this.userService.getCurrentUser().getCart().getId());
       this.cartService.updateTotalPrice(this.userService.getCurrentUser().getCart());
    }

    @Override
    public void deleteByName(String name) {
        this.courierRepository.deleteByName(name);
    }
}
