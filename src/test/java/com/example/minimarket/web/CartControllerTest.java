package com.example.minimarket.web;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.*;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.UserService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private CartService cartService;

    @Test
    public void testViewCart() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/carts/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("view-cart"));
    }

    @Test
    public void testView() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/carts/view"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testBuyCart() throws Exception {
        authenticate();
        getCourierAndAddress();
        this.mockMvc
                .perform(get("/carts/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testDeleteCartById() throws Exception {
        authenticate();
        getCourierAndAddress();
        this.mockMvc
                .perform(get("/carts/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

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

    public void getCourierAndAddress(){
        this.courierRepository.deleteAll();
        this.addressRepository.deleteAll();
        CourierEntity courierEntity;
        AddressEntity addressEntity1;
        courierEntity = new CourierEntity();
        courierEntity.setName("DHL");
        courierEntity.setImageUrl("image");
        courierEntity.setRating(5);
        courierEntity.setShippingAmount(BigDecimal.valueOf(5.5));

        this.courierRepository.save(courierEntity);

        addressEntity1 = new AddressEntity();
        addressEntity1.setStreetName("Shipka");
        addressEntity1.setStreetNumber(5);
        addressEntity1.setCountry("Bulgaria");
        addressEntity1.setCity("Plovdiv");
        addressEntity1.setZipCode("6000");
        addressEntity1.setDateTime(LocalDateTime.now());
        addressEntity1.setPaymentAmount(BigDecimal.valueOf(33));
        addressEntity1.setDelivered(true);
        addressEntity1.setCourier(courierEntity.getName());

        this.addressRepository.save(addressEntity1);

        this.cartService.setCourier(courierEntity, this.userService.getCurrentUser().getCart().getId());
        this.cartService.setAddress(addressEntity1, this.userService.getCurrentUser().getCart().getId());
    }
}
