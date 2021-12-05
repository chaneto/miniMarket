package com.example.minimarket.services.impl;

import com.example.minimarket.model.bindings.ProductAddBindingModel;
import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.ProductServiceModel;
import com.example.minimarket.model.views.ProductViewModel;
import com.example.minimarket.repositories.ProductRepository;
import com.example.minimarket.services.*;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper mapper;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final Gson gson;
    private final Resource productFile;
    private List<ProductViewModel> currentList = new ArrayList<>();

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper mapper, CategoryService categoryService
            , BrandService brandService, Gson gson,@Value("classpath:init/products.json") Resource productFile) {
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.gson = gson;
        this.productFile = productFile;
    }

    @Override
    public ProductViewModel findByName(String name) {
        ProductEntity product = this.productRepository.findByName(name);
        ProductViewModel productViewModel = null;
        if(product != null){
            productViewModel = this.mapper.map(product, ProductViewModel.class);
            productViewModel.setCategory(product.getCategory().getName());
            productViewModel.setBrand(product.getBrand().getName());
        }
        return productViewModel;
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
        productEntity.setOnPromotion(false);
        productEntity.setDiscountRate(BigDecimal.valueOf(0));
        productEntity.setPromotionPrice(productServiceModel.getPrice());
        this.productRepository.save(productEntity);
    }

    @Override
    public void seedProductsFromJson() throws IOException {
        if(this.productRepository.count() == 0){
            ProductAddBindingModel[] products = this.gson.fromJson(Files.readString(Path.of(productFile.getURI())), ProductAddBindingModel[].class);
            for(ProductAddBindingModel product: products){
                if(findByName(product.getName()) == null){
                seedProduct(this.mapper.map(product, ProductServiceModel.class));
                }
            }
        }
    }

    public List<ProductViewModel> findAllOrderByName(){
       List<ProductEntity> products = this.productRepository.findAllOrderByName();
        return conversionToListViewModel(products);
    }

    @Override
    public List<ProductViewModel> getPromotionProduct(){
        List<ProductEntity> products = this.productRepository.findAllByIsOnPromotionIsTrue();
        return conversionToListViewModel(products);
    }

    @Override
    public List<ProductEntity> findAllByIsOnPromotionIsTrue() {
        return this.productRepository.findAllByIsOnPromotionIsTrue();
    }

    public List<ProductViewModel> conversionToListViewModel(List<ProductEntity> products){
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
    public List<String> getAllProductsName() {
        return this.productRepository.getAllProductsName();
    }

    @Override
    public List<ProductViewModel> getAllOrderByID(){
       return conversionToListViewModel(this.productRepository.findAll());
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

    @Override
    public void setPrice(BigDecimal newPrice, String productName){
        this.productRepository.setPrice(newPrice, productName);
        setPromotionPriceAndDiscountRate(findByName(productName).getDiscountRate(), productName);
    }

    @Override
    public void setPromotionPrice(BigDecimal promotionPrice, String productName) {
        this.productRepository.setPromotionPrice(promotionPrice, productName);
    }

    @Override
    public void setDiscountRate(BigDecimal discountRate, String productName) {
        this.productRepository.setDiscountRate(discountRate, productName);
    }

    @Override
    public void setIsOnPromotion(Boolean isOnPromotion, Long productId){
        this.productRepository.setIsOnPromotion(isOnPromotion, productId);
    }

    @Override
    public void setPromotionPriceAndDiscountRate(BigDecimal discountRate, String productName) {
        ProductEntity currentProduct = this.productRepository.findByName(productName);
        if(discountRate.compareTo(BigDecimal.valueOf(0)) == 0){
            setPromotionPrice(currentProduct.getPrice(), productName);
            setIsOnPromotion(false, currentProduct.getId());
        }else{
            BigDecimal percent = discountRate.divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING);
            setPromotionPrice(currentProduct.getPrice().subtract(percent.multiply(currentProduct.getPrice())), productName);
            setIsOnPromotion(true, currentProduct.getId());
        }
        setDiscountRate(discountRate, productName);

    }

    @Override
    public List<ProductViewModel> findAllProductsOrderByQuantity() {
        return conversionToListViewModel(this.productRepository.findAllOrderByQuantity());
    }

    @Override
    public List<ProductViewModel> findAllProductsOrderByQuantityDesc() {
        return conversionToListViewModel(this.productRepository.findAllOrderByQuantityDesc());
    }

    @Override
    public List<ProductViewModel> findAllProductsOrderByPrice() {
        return conversionToListViewModel(this.productRepository.findAllOrderByPrice());
    }

    @Override
    public List<ProductViewModel> findAllProductsOrderByPriceDesc() {
        return conversionToListViewModel(this.productRepository.findAllOrderByPriceDesc());
    }

    @Override
    public ProductViewModel getById(Long id) {
        return this.mapper.map(this.productRepository.getById(id), ProductViewModel.class);
    }

    @Override
    public List<ProductViewModel> findTop4ByQuantityDesc() {
        return conversionToListViewModel(this.productRepository.findTop4ByQuantityDesc());
    }

    @Override
    public List<ProductViewModel> findTop4ByQuantity(){
        return findTop4ByQuantityDesc();
    }

    @Override
    public void updatePriceTop4ByQuantityProduct(){
        for(ProductViewModel product: currentList) {
           setPromotionPriceAndDiscountRate(BigDecimal.valueOf(50), product.getName());
        }
    }

    @Override
    public void updateTop4ByQuantityProduct(){
            for(ProductViewModel product: currentList){
                setPromotionPriceAndDiscountRate(BigDecimal.valueOf(0), product.getName());
            }

        currentList = findTop4ByQuantity();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void refreshPromotionProduct() {
        updateTop4ByQuantityProduct();
        updatePriceTop4ByQuantityProduct();
        System.out.println("Refresh promotion product");
    }
}
