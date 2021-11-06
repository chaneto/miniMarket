package com.example.minimarket.services;

import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.entities.CategoryEntity;
import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.model.services.ProductServiceModel;
import com.example.minimarket.model.views.ProductViewModel;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    boolean quantityIsEnough(String name, BigDecimal quantity);

    ProductServiceModel findByName(String name);

    void seedProduct(ProductServiceModel productServiceModel);

    List<ProductViewModel> findAll();

    void deleteProduct(String name);

    void addQuantity(BigDecimal quantity, String name);

    List<String> getAllProductsName();

    void buyProduct(String name, BigDecimal quantity);

    void subtractQuantity(BigDecimal quantity, String name);

    List<ProductViewModel> findAllByCategory(String categoryName);

    List<ProductViewModel> findAllByBrand(String brandName);
}
