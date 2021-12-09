package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.entities.CartEntity;
import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.services.AddressServiceModel;
import com.example.minimarket.model.views.AddressViewModel;
import com.example.minimarket.repositories.AddressRepository;
import com.example.minimarket.services.AddressService;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
        addressEntity.setUser(this.mapper.map(this.userService.getCurrentUser(), UserEntity.class));
        addressEntity.setDateTime(LocalDateTime.now());
        addressEntity.setDelivered(false);
        addressEntity.setPaymentAmount(BigDecimal.valueOf(0));
        addressEntity.setCourier(this.userService.getCurrentCart().getCourier().getName());
        this.addressRepository.save(addressEntity);
        this.cartService.setAddress(addressEntity, this.userService.getCurrentCartId());
    }

    @Override
    public void updateFinallyPaymentAmount(){
        this.addressRepository.setPaymentAmount(this.userService.getCurrentCart().getTotalPrice(), this.userService.getCurrentCart().getAddress().getId());
    }

    @Override
    public void deleteById(Long id) {
        this.addressRepository.deleteById(id);
    }

    @Override
    public List<AddressViewModel> getAllAddressesByCurrentUser(){
        return conversionToListViewModel(findAllByUserId(this.userService.getCurrentUser().getId()));
    }

    @Override
    public List<AddressViewModel> getAllNotDeliveredAddresses() {
        List<AddressViewModel> result = new ArrayList<>();
        List<AddressViewModel> addresses = conversionToListViewModel
                (this.addressRepository.findAllByIsDeliveredOrderByDateTime(false));
        for (AddressViewModel address: addresses){
            address.setOrders(this.orderService.findAllByAddressId(address.getId()));
            result.add(address);
        }
        return result;
    }

    @Override
    public void setOrdersToDelivered(Long id) {
        this.addressRepository.setIsDelivered(true, id);
        this.orderService.setOrdersToDelivered(id);
        this.orderService.setOrdersToPaid(id);
    }

    public List<AddressViewModel> conversionToListViewModel(List<AddressEntity> addresses){
        List<AddressViewModel> addressesView = new ArrayList<>();
        for(AddressEntity address: addresses){
            AddressViewModel addressViewModel = this.mapper.map(address, AddressViewModel.class);
            String date  = addressViewModel.getDateTime().substring(0,10) + " " + addressViewModel.getDateTime().substring(11, 19);
            addressViewModel.setDateTime(date);
            addressesView.add(addressViewModel);
        }
        return  addressesView;
    }

    @Override
    public List<AddressEntity> findAllByUserId(Long userId) {
        return this.addressRepository.findAllByUserId(userId);
    }

    public List<AddressEntity> findAllWithDateIsSmaller1Year(){
        return this.addressRepository.findAllWithDateIsSmaller(LocalDateTime.now().minus(1, ChronoUnit.YEARS));
       // return this.addressRepository.findAllWithDateIsSmaller(LocalDateTime.now().minus(3, ChronoUnit.MINUTES));
    }

    public void deleteAllAddressesOlderThan1Year(){
        for(AddressEntity address : findAllWithDateIsSmaller1Year()){
            if(!cartService.cartWithOrderNotDeliveredToAddress(address) && address.isDelivered()){
            deleteById(address.getId());
            }
        }
    }

   // @Scheduled(cron = "0 */3 * * * *")
   @Scheduled(cron = "0 5 0 * * *")
    public void addressCleaning() {
        deleteAllAddressesOlderThan1Year();
        System.out.println("Address cleaning");
    }
}
