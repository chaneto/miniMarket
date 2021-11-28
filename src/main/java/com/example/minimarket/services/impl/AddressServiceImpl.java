package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.entities.OrderEntity;
import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.services.AddressServiceModel;
import com.example.minimarket.repositories.AddressRepository;
import com.example.minimarket.services.AddressService;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapper mapper;
    private final UserService userService;
    private final CartService cartService;

    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper mapper, UserService userService, CartService cartService) {
        this.addressRepository = addressRepository;
        this.mapper = mapper;
        this.userService = userService;
        this.cartService = cartService;
    }

    @Override
    public void save(AddressServiceModel addressServiceModel) {
        AddressEntity addressEntity = this.mapper.map(addressServiceModel, AddressEntity.class);
        addressEntity.setUser(this.mapper.map(this.userService.getCurrentUser(), UserEntity.class));
        addressEntity.setDateTime(LocalDateTime.now());
        this.addressRepository.save(addressEntity);
        this.cartService.setAddress(addressEntity, this.userService.getCartId());
    }

    @Override
    public void deleteById(Long id) {
        this.addressRepository.deleteById(id);
    }

    public List<AddressEntity> findAllWithDateIsSmaller6Months(){
        return this.addressRepository.findAllWithDateIsSmaller(LocalDateTime.now().minus(6, ChronoUnit.MONTHS));
       // return this.addressRepository.findAllWithDateIsSmaller(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));
    }

    public void deleteAllAddressesOlderThan6Months(){
        for(AddressEntity address : findAllWithDateIsSmaller6Months()){
            if(!cartService.cartWithOrderNotDeliveredToAddress(address)){
            deleteById(address.getId());
            }
        }
    }

    @Scheduled(cron = "0 0 0 1 * *")
   // @Scheduled(cron = "0 */3 * * * *")
    public void addressCleaning() {
        deleteAllAddressesOlderThan6Months();
        System.out.println("Address cleaning");
    }
}
