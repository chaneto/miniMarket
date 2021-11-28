package com.example.minimarket.services;

import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.services.BrandServiceModel;
import com.example.minimarket.model.views.ProductViewModel;

import java.io.IOException;
import java.util.List;

public interface BrandService {

    List<ProductViewModel> getAllProducts(String name);

    BrandServiceModel findByName(String name);

    BrandEntity findByNameEntity(String name);

    List<BrandServiceModel> getAllBrands();

    void deleteByName(String name);

    void seedBrandsFromJson() throws IOException;

    void saveBrand(BrandServiceModel brandServiceModel);
}
