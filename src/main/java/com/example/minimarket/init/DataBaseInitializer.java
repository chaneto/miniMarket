package com.example.minimarket.init;

import com.example.minimarket.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataBaseInitializer implements CommandLineRunner {

    private final BrandService brandService;
    private final UserRoleService userRoleService;
    private final CategoryService categoryService;
    private final CourierService courierService;
    private final ProductService productService;
    private final UserService userService;


    public DataBaseInitializer(BrandService brandService, UserRoleService userRoleService, CategoryService categoryService, CourierService courierService, ProductService productService, UserService userService) {
        this.brandService = brandService;
        this.userRoleService = userRoleService;
        this.categoryService = categoryService;
        this.courierService = courierService;
        this.productService = productService;
        this.userService = userService;
    }


    @Override
    public void run(String... args) throws Exception {
        this.userRoleService.seedUsersRoleEntity();
        this.categoryService.seedCategoriesFromJson();
        this.brandService.seedBrandsFromJson();
        this.courierService.seedCourierFromJson();
        this.productService.seedProductsFromJson();
        this.userService.seedUsersFromJson();
        if(productService.getPromotionProduct().size() == 0){
        this.productService.refreshPromotionProduct();
        }
    }
}
