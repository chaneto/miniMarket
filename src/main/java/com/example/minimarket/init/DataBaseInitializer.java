package com.example.minimarket.init;

import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.services.BrandService;
import com.example.minimarket.services.CategoryService;
import com.example.minimarket.services.CourierService;
import com.example.minimarket.services.UserRoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataBaseInitializer implements CommandLineRunner {

    private final BrandService brandService;
    private final UserRoleService userRoleService;
    private final CategoryService categoryService;
    private final CourierService courierService;


    public DataBaseInitializer(BrandService brandService, UserRoleService userRoleService, CategoryService categoryService, CourierService courierService) {
        this.brandService = brandService;
        this.userRoleService = userRoleService;
        this.categoryService = categoryService;
        this.courierService = courierService;
    }


    @Override
    public void run(String... args) throws Exception {
        this.userRoleService.seedUsersRoleEntity();
        this.categoryService.seedCategories();
        this.brandService.seedBrands();
        this.courierService.seedCourier();
    }
}
