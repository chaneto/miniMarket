package com.example.minimarket.services;

import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.model.services.ProductServiceModel;
import com.example.minimarket.model.views.ProductViewModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    void setIsOnPromotion(Boolean IsOnPromotion, Long productId);

    void seedProductsFromJson() throws IOException;

    boolean quantityIsEnough(String name, BigDecimal quantity);

    ProductViewModel findByName(String name);

    ProductEntity findByNameEntity(String name);

    void seedProduct(ProductServiceModel productServiceModel);

    List<ProductViewModel> findAllOrderByName();

    void deleteProductByName(String name);

    void addQuantity(BigDecimal quantity, String name);

    List<String> getAllProductsName();

    void buyProduct(String name, BigDecimal quantity);

    void subtractQuantity(BigDecimal quantity, String name);

    List<ProductViewModel> findTop4ByQuantityDesc();

    List<ProductViewModel> findTop4ByQuantity();

    List<ProductEntity> findAllByIsOnPromotionIsTrue();

    List<ProductViewModel> getPromotionProduct();

    ProductViewModel getById(Long id);

    void setPrice(BigDecimal newPrice, String productName);

    void setPromotionPrice(BigDecimal promotionPrice, String productName);

    void setDiscountRate(BigDecimal discountRate, String productName);

    List<ProductViewModel> findAllProductsOrderByQuantity();

    void updatePriceTop4ByQuantityProduct();

    void updateTop4ByQuantityProduct();

    void refreshPromotionProduct();

    void setPromotionPriceAndDiscountRate(BigDecimal discountRate, String productName);

    List<ProductViewModel> findAllProductsOrderByQuantityDesc();

    List<ProductViewModel> findAllProductsOrderByPrice();

    List<ProductViewModel> findAllProductsOrderByPriceDesc();

    List<ProductViewModel> getAllOrderByID();
}
