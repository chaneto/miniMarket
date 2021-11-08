package com.example.minimarket.services.impl;

import com.example.minimarket.model.bindings.BrandAddBidingModel;
import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.model.services.BrandServiceModel;
import com.example.minimarket.model.views.ProductViewModel;
import com.example.minimarket.repositories.BrandRepository;
import com.example.minimarket.services.BrandService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final Resource brandFile;

    public BrandServiceImpl(@Value("classpath:init/brands.json") Resource brandFile, BrandRepository brandRepository, ModelMapper mapper, Gson gson) {
        this.brandRepository = brandRepository;
        this.mapper = mapper;
        this.gson = gson;
        this.brandFile = brandFile;
    }

    @Override
    public BrandServiceModel findByName(String name) {
        if(this.brandRepository.findByName(name) != null){
        return this.mapper.map(this.brandRepository.findByName(name), BrandServiceModel.class);
        }else {
            return null;
        }
    }

    @Override
    public void seedBrands() throws IOException {
        if(this.brandRepository.count() == 0){
            BrandAddBidingModel[] brands = this.gson.fromJson(Files.readString(Path.of(brandFile.getURI())), BrandAddBidingModel[].class);
            for(BrandAddBidingModel brand: brands){
                BrandEntity brandEntity = this.mapper.map(brand, BrandEntity.class);
                this.brandRepository.save(brandEntity);
            }
        }
    }

    @Override
    public BrandEntity findByNameEntity(String name) {
        return this.brandRepository.findByName(name);
    }

    @Override
    public List<BrandServiceModel> getAllBrands(){
        return this.brandRepository.getAllBrands()
                .stream()
                .map(m -> this.mapper.map(m, BrandServiceModel.class)).collect(Collectors.toList());

    }

    @Override
    public List<ProductViewModel> getAllProducts(String name){
        BrandEntity brand = this.findByNameEntity(name);
        List<ProductEntity> products = brand.getProducts();
        List<ProductViewModel> productsViews = new ArrayList<>();
        for(ProductEntity product : products){
            ProductViewModel productViewModel = this.mapper.map(product, ProductViewModel.class);
            productViewModel.setCategory(product.getCategory().getName());
            productViewModel.setBrand(product.getBrand().getName());
            productsViews.add(productViewModel);
        }
        return productsViews;
    }

    @Override
    public void saveBrand(BrandServiceModel brandServiceModel) {
        this.brandRepository.save(this.mapper.map(brandServiceModel, BrandEntity.class));
    }

    @Override
    public void deleteByName(String name) {
        this.brandRepository.deleteByName(name);
    }
}
