package com.example.minimarket.services;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.services.AddressServiceModel;

import java.util.List;

public interface AddressService {

    void save(AddressServiceModel addressServiceModel);

    void deleteById(Long id);

    List<AddressEntity> findAllWithDateIsSmaller6Months();

    void deleteAllAddressesOlderThan6Months();

    void addressCleaning();
}
