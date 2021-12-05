package com.example.minimarket.web;


import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.model.entities.OrderEntity;
import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.CourierRepository;
import com.example.minimarket.repositories.OrderRepository;
import com.example.minimarket.repositories.ProductRepository;
import com.example.minimarket.services.UserService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;

    OrderEntity orderEntity;
    ProductEntity productEntity;

    @Before
    public void setup(){
        this.productRepository.deleteAll();
        this.orderRepository.deleteAll();

        productEntity = new ProductEntity();
        productEntity.setName("case");
        productEntity.setPrice(BigDecimal.valueOf(2.50));
        productEntity.setDescription("The best product...");
        productEntity.setQuantity(BigDecimal.valueOf(20));
        productEntity.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        productEntity.setOnPromotion(true);
        productEntity.setDiscountRate(BigDecimal.valueOf(33));
        productEntity.setPromotionPrice(BigDecimal.valueOf(33));
        this.productRepository.save(productEntity);

         orderEntity = new OrderEntity();
         orderEntity.setTotalPrice(BigDecimal.valueOf(9));
         orderEntity.setDateTime(LocalDateTime.now());
         orderEntity.setPaid(true);
         orderEntity.setProduct(productEntity);
         orderEntity.setOrdered(true);
         orderEntity.setDelivered(true);
         orderEntity.setUsername("admin");
         this.orderRepository.save(orderEntity);

    }

    @Test
    public void testDeleteOrder() throws Exception {
       this.mockMvc
               .perform(get("/orders/delete/1")
                       .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testGetAllIsNotDeliveredOrders() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/orders/allNotDelivered")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("orders"))
        .andExpect(view().name("all-orders"));
    }

    @Test
    public void testGetAllDeliveredOrders() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/orders/allDelivered")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("orders"))
                .andExpect(view().name("all-orders"));
    }

    @Test
    public void testGetAll() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/orders/all")
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("orders"))
                .andExpect(view().name("all-orders"));
    }
    
    @Test
    public void testOrderHistory() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/orders/history")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("order-history"))
                .andExpect(model().attributeExists("ordersHistory"));


    }

    public void authenticate(){

        UserRegisterServiceModel userRegisterServiceModel = new UserRegisterServiceModel();
        userRegisterServiceModel.setUsername("admin");
        userRegisterServiceModel.setFirstName("Admin");
        userRegisterServiceModel.setLastName("Adminov");
        userRegisterServiceModel.setEmail("admin_80@abv.bg");
        userRegisterServiceModel.setPassword("12345");
        userRegisterServiceModel.setConfirmPassword("12345");
        userRegisterServiceModel.setPhoneNumber("123456789");
        if(!this.userService.userWithUsernameIsExists("admin_80@abv.bg") && !this.userService.userWithUsernameIsExists("admin")){
            this.userService.registerUser(userRegisterServiceModel);
        }
        UserLoginServiceModel userLoginServiceModel = new UserLoginServiceModel();
        userLoginServiceModel.setUsername("admin");
        userLoginServiceModel.setPassword("12345");
        this.userService.authenticate(userLoginServiceModel);
    }
}
