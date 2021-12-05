package com.example.minimarket.web;

import com.example.minimarket.model.entities.CategoryEntity;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.CategoryRepository;
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
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserService userService;

    CategoryEntity categoryEntity;

    @Before
    public void setup(){
        this.categoryRepository.deleteAll();

         categoryEntity = new CategoryEntity();
         categoryEntity.setName("chargers");
         categoryEntity.setDescription("Chargers for all smartphone");
         categoryEntity.setImage("image");

         this.categoryRepository.save(categoryEntity);
    }

    @Test
    public void testAddCategory() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/categories/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-category"))
        .andExpect(model().attributeExists("categoryAddBindingModel"))
        .andExpect(model().attributeExists("categoryIsExists"));
    }

    @Test
    public void testAddCategoryConfirm() throws Exception {
        Assert.assertEquals(1, this.categoryRepository.count());
        authenticate();
        this.mockMvc
                .perform(post("/categories/add")
                .param("name", "cases")
                .param("description", "Chargers for all smartphone")
                .param("image", "image")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));

        Assert.assertEquals(2, this.categoryRepository.count());
    }

    @Test
    public void testAddCategoryConfirmWithExistsCategory() throws Exception {
        Assert.assertEquals(1, this.categoryRepository.count());
        authenticate();
        this.mockMvc
                .perform(post("/categories/add")
                        .param("name", "chargers")
                        .param("description", "Chargers for all smartphone")
                        .param("image", "image")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));

        Assert.assertEquals(1, this.categoryRepository.count());
    }

    @Test
    public void testAddCategoryConfirmWithInvalidData() throws Exception {
        Assert.assertEquals(1, this.categoryRepository.count());
        authenticate();
        this.mockMvc
                .perform(post("/categories/add")
                        .param("name", "c")
                        .param("description", "C")
                        .param("image", "image")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));

        Assert.assertEquals(1, this.categoryRepository.count());
    }


    @Test
    public void testAllCategory() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/categories/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-categories"))
                .andExpect(model().attributeExists("allCategories"));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        authenticate();
        Assert.assertEquals(1, this.categoryRepository.count());
        this.mockMvc
                .perform(get("/categories/delete/chargers")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
        Assert.assertEquals(0, this.categoryRepository.count());
    }

    @Test
    public void testGetAllProductsByCategory() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/categories/allByCategory/chargers"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-products"))
                .andExpect(model().attributeExists("allProducts"));
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
