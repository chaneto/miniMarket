package com.example.minimarket.services;

import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.model.services.ProductServiceModel;
import com.example.minimarket.model.views.ProductViewModel;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    boolean quantityIsEnough(String name, BigDecimal quantity);

    ProductServiceModel findByName(String name);

    ProductEntity findByNameEntity(String name);

    void seedProduct(ProductServiceModel productServiceModel);

    List<ProductViewModel> findAll();

    void deleteProductByName(String name);

    void addQuantity(BigDecimal quantity, String name);

    List<String> getAllProductsByName();

    void buyProduct(String name, BigDecimal quantity);

    void subtractQuantity(BigDecimal quantity, String name);

}
