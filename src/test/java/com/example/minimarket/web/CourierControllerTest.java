package com.example.minimarket.web;

import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.CourierRepository;
import com.example.minimarket.services.UserService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CourierControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private UserService userService;

    CourierEntity courierEntity;
    UserRegisterServiceModel userRegisterServiceModel;
    UserLoginServiceModel userLoginServiceModel = null;

    @BeforeEach
    public void setup(){
        this.courierRepository.deleteAll();

        courierEntity = new CourierEntity();
        courierEntity.setName("dhl");
        courierEntity.setRating(5);
        courierEntity.setShippingAmount(BigDecimal.valueOf(5.5));
        courierEntity.setImageUrl("image");

        this.courierRepository.save(courierEntity);
    }


    @Test
    public void testAddCourier() throws Exception {
            authenticate();
        this.mockMvc
                .perform(get("/couriers/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-courier"))
                .andExpect(model().attributeExists("courierAddBindingModel"))
                .andExpect(model().attributeExists("courierIsExists"));
    }

    @Test
    public void testAddCourierConfirm() throws Exception {
            authenticate();
        this.mockMvc
                .perform(post("/couriers/add")
                .param("name", "speedy")
                .param("rating", "5")
                .param("shippingAmount", "5.5")
                .param("imageUrl", "image")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
    }

    @Test
    public void testAddCourierConfirmWithInvalidData() throws Exception {
            authenticate();
        this.mockMvc
                .perform(post("/couriers/add")
                        .param("name", "d")
                        .param("rating", "null")
                        .param("shippingAmount", "5.5")
                        .param("imageUrl", "image")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
    }

    @Test
    public void testAddCourierConfirmWithExistingCourier() throws Exception {
            authenticate();
        this.mockMvc
                .perform(post("/couriers/add")
                        .param("name", "dhl")
                        .param("rating", "5")
                        .param("shippingAmount", "5.5")
                        .param("imageUrl", "image")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
    }

    @Test
    public void testAllCouriersView() throws Exception {
        this.mockMvc
                .perform(get("/couriers/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-couriers"))
                .andExpect(model().attributeExists("allCouriers"));

    }

    @Test
    public void testDeleteCourier() throws Exception {
            authenticate();
        this.mockMvc
                .perform(get("/couriers/delete/dhl")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testAddCourierToCartWithInvalidName() throws Exception {
           authenticate();
        this.mockMvc
                .perform(post("/couriers/get")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:get"));
    }

    public void authenticate(){
        userRegisterServiceModel = new UserRegisterServiceModel();
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
        userLoginServiceModel = new UserLoginServiceModel();
        userLoginServiceModel.setUsername("admin");
        userLoginServiceModel.setPassword("12345");
        this.userService.authenticate(userLoginServiceModel);

    }
}
