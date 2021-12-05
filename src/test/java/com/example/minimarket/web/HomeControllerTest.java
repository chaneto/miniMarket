package com.example.minimarket.web;


import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.*;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;

    ProductEntity productEntity1;
    BrandEntity brandEntity;
    CategoryEntity categoryEntity;
    CartEntity cartEntity;
    OrderEntity orderEntity;

    @Before
    public void setup(){
        this.categoryRepository.deleteAll();
        this.brandRepository.deleteAll();
        this.productRepository.deleteAll();
        this.cartRepository.deleteAll();
        this.orderRepository.deleteAll();

        categoryEntity = new CategoryEntity();
        categoryEntity.setName("Protectors");
        categoryEntity.setDescription("Protectors for all smartphones");
        categoryEntity.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        this.categoryRepository.save(categoryEntity);

        brandEntity = new BrandEntity();
        brandEntity.setName("Nokia");
        brandEntity.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        this.brandRepository.save(brandEntity);

        productEntity1 = new ProductEntity();
        productEntity1.setName("case");
        productEntity1.setPrice(BigDecimal.valueOf(2.50));
        productEntity1.setDescription("The best product...");
        productEntity1.setQuantity(BigDecimal.valueOf(20));
        productEntity1.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        productEntity1.setOnPromotion(true);
        productEntity1.setCategory(categoryEntity);
        productEntity1.setBrand(brandEntity);
        productEntity1.setPromotionPrice(BigDecimal.valueOf(33));
        productEntity1.setDiscountRate(BigDecimal.valueOf(22));
        this.productRepository.save(productEntity1);

        cartEntity = new CartEntity();
        cartEntity.setTotalPrice(BigDecimal.valueOf(99));
        this.cartRepository.save(cartEntity);


        orderEntity = new OrderEntity();
        orderEntity.setProduct(productEntity1);
        orderEntity.setCart(cartEntity);
        orderEntity.setTotalPrice(BigDecimal.valueOf(99));
        orderEntity.setDateTime(LocalDateTime.now());
        orderEntity.setOrdered(true);
        orderEntity.setUsername("admin");
        List<OrderEntity> orders = List.of(orderEntity);
        cartEntity.setOrders(orders);
        this.orderRepository.save(orderEntity);

    }


    @Test
    public void testIndex() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void testHome() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void testContacts() throws Exception {
        this.mockMvc
                .perform(get("/contacts"))
                .andExpect(status().isOk())
                .andExpect(view().name("contacts"));
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
