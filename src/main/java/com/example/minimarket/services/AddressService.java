package com.example.minimarket.services;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.services.AddressServiceModel;
import com.example.minimarket.model.views.AddressViewModel;

import java.util.List;

public interface AddressService {

    List<AddressViewModel> conversionToListViewModel(List<AddressEntity> addresses);

    List<AddressEntity> findAllByUserId(Long userId);

    void updateFinallyPaymentAmount();

    void save(AddressServiceModel addressServiceModel);

    void deleteById(Long id);

    List<AddressEntity> findAllWithDateIsSmaller6Months();

    void deleteAllAddressesOlderThan6Months();

    void addressCleaning();

    List<AddressViewModel> getAllAddressesByCurrentUser();

    List<AddressViewModel> getAllNotDeliveredAddresses();

    void setOrdersToDelivered(Long id);
}
