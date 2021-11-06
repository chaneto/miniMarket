package com.example.minimarket.services;

import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.services.BrandServiceModel;

import java.io.IOException;
import java.util.List;

public interface BrandService {

    BrandServiceModel findByName(String name);

    BrandEntity findByNameEntity(String name);

    List<BrandServiceModel> getAllBrands();

    void deleteByName(String name);

    void seedBrands() throws IOException;

    void saveBrand(BrandServiceModel brandServiceModel);
}
