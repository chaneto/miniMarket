package com.example.minimarket.web;

import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.BrandRepository;
import com.example.minimarket.services.UserService;
import org.junit.Assert;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private UserService userService;

    BrandEntity brandEntity;

    @Before
    public void setup(){
        this.brandRepository.deleteAll();

        brandEntity = new BrandEntity();
        brandEntity.setName("Nokia");
        brandEntity.setImage("image");

        this.brandRepository.save(brandEntity);
    }

    @Test
    public void testAddBrand() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/brands/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-brand"))
                .andExpect(model().attributeExists("brandAddBidingModel"))
                .andExpect(model().attributeExists("brandIsExists"));
    }

    @Test
    public void testAddBrandConfirm() throws Exception {
        Assert.assertEquals(1, this.brandRepository.count());
        authenticate();
        this.mockMvc
                .perform(post("/brands/add")
                .param("name", "Apple")
                .param("image", "image")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
        Assert.assertEquals(2, this.brandRepository.count());

    }

    @Test
    public void testAddBrandConfirmWithExistingBrand() throws Exception {
        Assert.assertEquals(1, this.brandRepository.count());
        authenticate();
        this.mockMvc
                .perform(post("/brands/add")
                        .param("name", "Nokia")
                        .param("image", "image")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
        Assert.assertEquals(1, this.brandRepository.count());

    }

    @Test
    public void testAddBrandConfirmWithInvalidData() throws Exception {
        Assert.assertEquals(1, this.brandRepository.count());
        authenticate();
        this.mockMvc
                .perform(post("/brands/add")
                        .param("name", "")
                        .param("image", "image")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
        Assert.assertEquals(1, this.brandRepository.count());

    }

    @Test
    public void testAllBrand() throws Exception {
        this.mockMvc
                .perform(get("/brands/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-brands"))
                .andExpect(model().attributeExists("allBrands"));
    }

    @Test
    public void testDeleteBrand() throws Exception {
        Assert.assertEquals(1, this.brandRepository.count());
        authenticate();
        this.mockMvc
                .perform(get("/brands/delete/Nokia")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
        Assert.assertEquals(0, this.brandRepository.count());
    }


    @Test
    public void testGetAllProductsByCategory() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/brands/allByBrand/Nokia"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-products-by-brands"))
                .andExpect(model().attributeExists("getAllProductsByBrand"));
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
        this.userService.registerUser(userRegisterServiceModel);
        UserLoginServiceModel userLoginServiceModel = new UserLoginServiceModel();
        userLoginServiceModel.setUsername("admin");
        userLoginServiceModel.setPassword("12345");
        this.userService.authenticate(userLoginServiceModel);
    }
}
