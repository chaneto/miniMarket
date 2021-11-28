package com.example.minimarket.web;

import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.entities.CategoryEntity;
import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.repositories.BrandRepository;
import com.example.minimarket.repositories.CategoryRepository;
import com.example.minimarket.repositories.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    ProductEntity productEntity;
    BrandEntity brandEntity;
    CategoryEntity categoryEntity;

    @Before
    public void setup(){
        productEntity = new ProductEntity();
        brandEntity = new BrandEntity();
        categoryEntity = new CategoryEntity();

        brandEntity.setName("nokia");
        brandEntity.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        brandRepository.save(brandEntity);

        categoryEntity.setName("charger");
        categoryEntity.setDescription("min10characters");
        categoryEntity.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        categoryRepository.save(categoryEntity);

        productEntity.setName("Battery");
        productEntity.setPrice(BigDecimal.valueOf(15));
        productEntity.setQuantity(BigDecimal.valueOf(11));
        productEntity.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        productEntity.setBrand(brandEntity);
        productEntity.setCategory(categoryEntity);

        this.productRepository.save(productEntity);
    }

    @Test
    public void testFindAll() throws Exception {
        ResultActions resultActions;
        resultActions = this.mockMvc
                .perform(get("/products/api"))
                .andExpect(status().isOk());



    }
}
