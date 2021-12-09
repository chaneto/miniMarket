package com.example.minimarket.web;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.*;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.ProductService;
import com.example.minimarket.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
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
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CourierRepository courierRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private WebApplicationContext context;

    ProductEntity productEntity1;
    ProductEntity productEntity3;
    CategoryEntity categoryEntity;
    BrandEntity brandEntity;
    OrderEntity orderEntity;
    CartEntity cartEntity;
    CourierEntity courierEntity;
    AddressEntity addressEntity;

    @Before
    public void setup(){
       this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        this.orderRepository.deleteAll();
        this.brandRepository.deleteAll();
        this.categoryRepository.deleteAll();
        this.productRepository.deleteAll();
        this.cartRepository.deleteAll();
        this.courierRepository.deleteAll();
        this.addressRepository.deleteAll();
        this.userRepository.deleteAll();

        brandEntity = new BrandEntity();
        brandEntity.setName("Nokia");
        brandEntity.setImage("image");
        this.brandRepository.save(brandEntity);

        categoryEntity = new CategoryEntity();
        categoryEntity.setName("cases");
        categoryEntity.setDescription("Cases for all smartphones...");
        categoryEntity.setImage("image");
        this.categoryRepository.save(categoryEntity);

        productEntity1 = new ProductEntity();
        productEntity1.setName("case");
        productEntity1.setPrice(BigDecimal.valueOf(10));
        productEntity1.setQuantity(BigDecimal.valueOf(100));
        productEntity1.setImage("image");
        productEntity1.setDescription("The best product...");
        productEntity1.setOnPromotion(true);
        productEntity1.setCategory(categoryEntity);
        productEntity1.setBrand(brandEntity);
        productEntity1.setDiscountRate(BigDecimal.valueOf(20));
        productEntity1.setPromotionPrice(BigDecimal.valueOf(11));

        productEntity3 = new ProductEntity();
        productEntity3.setName("TPU");
        productEntity3.setPrice(BigDecimal.valueOf(30));
        productEntity3.setQuantity(BigDecimal.valueOf(300));
        productEntity3.setImage("image");
        productEntity3.setDescription("The best product...");
        productEntity3.setOnPromotion(false);
        productEntity3.setCategory(categoryEntity);
        productEntity3.setBrand(brandEntity);
        productEntity3.setDiscountRate(BigDecimal.valueOf(20));
        productEntity3.setPromotionPrice(BigDecimal.valueOf(11));

        this.productRepository.save(productEntity1);
        this.productRepository.save(productEntity3);


        courierEntity = new CourierEntity();
        courierEntity.setName("dhl");
        courierEntity.setRating(5);
        courierEntity.setShippingAmount(BigDecimal.valueOf(5.50));
        courierEntity.setImageUrl("image");
        this.courierRepository.save(courierEntity);

        addressEntity = new AddressEntity();
        addressEntity.setStreetName("Shipka");
        addressEntity.setStreetNumber(5);
        addressEntity.setCountry("Bulgaria");
        addressEntity.setCity("Plovdiv");
        addressEntity.setZipCode("6000");
        addressEntity.setDateTime(LocalDateTime.now());
        addressEntity.setPaymentAmount(BigDecimal.valueOf(33));
        addressEntity.setDelivered(true);
        this.addressRepository.save(addressEntity);

        authenticate();

        cartEntity = new CartEntity();
        cartEntity.setTotalPrice(BigDecimal.valueOf(0));
        cartEntity.setCourier(courierEntity);
        cartEntity.setAddress(addressEntity);
        List<OrderEntity> orders = new ArrayList<>();
        cartEntity.setOrders(orders);
        this.cartRepository.save(cartEntity);

        orderEntity = new OrderEntity();
        orderEntity.setDateTime(LocalDateTime.now());
        orderEntity.setTotalPrice(BigDecimal.valueOf(40));
        orderEntity.setProduct(productEntity1);
        orderEntity.setPaid(false);
        orderEntity.setProductCount(1);
        orderEntity.setCourier(courierEntity);
        orderEntity.setAddress(addressEntity);
        orderEntity.setCart(cartEntity);
        orderEntity.setUsername("Petko");
        orderEntity.setOrdered(true);
        orderEntity.setDelivered(false);
        this.orderRepository.save(orderEntity);

    }

    @Test
    public void testDeleteOrder() throws Exception {
        Long id = this.orderRepository.findAll().get(0).getId();
       this.mockMvc
               .perform(get("/orders/delete/{id}", "id")
               .with(csrf()))
               .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    public void testDeleteByAdminPanel() throws Exception {
        Long id = this.orderRepository.findAll().get(0).getId();
        authenticate();
        this.userService.setUserRole(this.userService.getCurrentUser().getUsername(), "ADMIN");
        this.mockMvc
                .perform(get("/orders/deleteByAdminPanel/id")
                        .with(csrf()))
               .andExpect(status().is4xxClientError());
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

    @Test
    @WithMockUser
    public void testSelectByUser() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/orders/selectByUser")
                .param("username", "admin")
                .with(csrf()))
                .andExpect(model().attributeExists("maxDate"))
                .andExpect(model().attributeExists("ordersByDateTimeBindingModel"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(view().name("all-orders"));
    }

    @Test
    @WithMockUser
    public void testSelectByUserWithNotExistinguser() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/orders/selectByUser")
                        .param("username", "Pencho")
                        .with(csrf()))
                .andExpect(view().name("redirect:/orders/all"));
    }

    @Test
    @WithMockUser
    public void testSelectByUserWithInvalidData() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/orders/selectByUser")
                        .param("username", "a")
                        .with(csrf()))
                .andExpect(view().name("redirect:/orders/all"));
    }

    @Test
    @WithMockUser
    public void testSelectByDate() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/orders/selectByDate")
                        .param("date", "2021-12-07")
                        .with(csrf()))
                .andExpect(model().attributeExists("maxDate"))
                .andExpect(model().attributeExists("ordersByDateTimeBindingModel"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(view().name("all-orders"));
    }

    @Test
    @WithMockUser
    public void testSelectByDateWithInvalidDate() throws Exception {
        LocalDate date = LocalDate.now();
        LocalDate dtPlusOne = date.plusDays(1);
        authenticate();
        this.mockMvc
                .perform(post("/orders/selectByDate")
                        .param("date", "dtPlusOne")
                        .with(csrf()))
                .andExpect(view().name("redirect:/orders/all"));
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
