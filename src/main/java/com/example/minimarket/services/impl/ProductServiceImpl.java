package com.example.minimarket.services.impl;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.ProductServiceModel;
import com.example.minimarket.model.views.ProductViewModel;
import com.example.minimarket.repositories.ProductRepository;
import com.example.minimarket.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper mapper;
    private final CategoryService categoryService;
    private final BrandService brandService;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper mapper, CategoryService categoryService, BrandService brandService) {
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    @Override
    public ProductServiceModel findByName(String name) {
        ProductEntity productEntity = this.productRepository.findByName(name);
        ProductServiceModel productServiceModel = null;
        if(productEntity != null){
            productServiceModel = this.mapper.map(productEntity, ProductServiceModel.class);
            productServiceModel.setBrand(productEntity.getBrand().getName());
            productServiceModel.setCategory(productEntity.getCategory().getName());
        }
        return productServiceModel;
    }

    @Override
    public ProductEntity findByNameEntity(String name) {
        return this.productRepository.findByName(name);
    }

    @Override
    public void seedProduct(ProductServiceModel productServiceModel) {
        ProductEntity productEntity = this.mapper.map(productServiceModel, ProductEntity.class);
        CategoryEntity categoryEntity = this.categoryService.findByNameEntity(productServiceModel.getCategory());
        BrandEntity brandEntity = this.brandService.findByNameEntity(productServiceModel.getBrand());
        if(productEntity.getQuantity().compareTo(BigDecimal.valueOf(0.0)) > 0){
            productEntity.setAvailable(true);
        }else {
            productEntity.setAvailable(false);
        }
        productEntity.setCategory(categoryEntity);
        productEntity.setBrand(brandEntity);
        this.productRepository.save(productEntity);
    }

    public List<ProductViewModel> findAll(){
       List<ProductEntity> products = this.productRepository.findAll();
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
    public void deleteProductByName(String name) {
        this.productRepository.deleteByName(name);
    }

    @Override
    public void addQuantity(BigDecimal quantity, String name) {
        this.productRepository.addQuantity(quantity, name);
        ProductEntity productEntity = this.productRepository.findByName(name);
        if(productEntity.getQuantity().compareTo(BigDecimal.valueOf(0.0)) > 0){
            this.productRepository.setIsAvailable(true, productEntity.getId());
        }
    }

    @Override
    public List<String> getAllProductsByName() {
        return this.productRepository.getAllProductsName();
    }

    @Override
    public void buyProduct(String name, BigDecimal quantity) {
        this.productRepository.subtractQuantity(quantity, name);
        ProductEntity productEntity = this.productRepository.findByName(name);
        if(productEntity.getQuantity().compareTo(BigDecimal.valueOf(0.0)) <= 0){
            this.productRepository.setIsAvailable(false, productEntity.getId());
        }
    }

    public boolean quantityIsEnough(String name, BigDecimal quantity){
      return this.productRepository.findByName(name).getQuantity().compareTo(quantity) >= 0;
    }

    @Override
    public void subtractQuantity(BigDecimal quantity, String name) {
        this.productRepository.subtractQuantity(quantity, name);
    }
}
