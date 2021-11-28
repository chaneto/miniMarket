package com.example.minimarket.web;

import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.UserRoleRepository;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    UserLoginServiceModel userLoginServiceModel;

    @Before
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        UserRegisterServiceModel userRegisterServiceModel = new UserRegisterServiceModel();
        userRegisterServiceModel.setUsername("admin");
        userRegisterServiceModel.setFirstName("Admin");
        userRegisterServiceModel.setLastName("Adminov");
        userRegisterServiceModel.setEmail("admin_80@abv.bg");
        userRegisterServiceModel.setPassword("12345");
        userRegisterServiceModel.setConfirmPassword("12345");
        userRegisterServiceModel.setPhoneNumber("123456789");
        this.userService.registerUser(userRegisterServiceModel);
        userLoginServiceModel = new UserLoginServiceModel();
        userLoginServiceModel.setUsername("admin");
        userLoginServiceModel.setPassword("12345");
        this.userService.authenticate(userLoginServiceModel);
    }

    @Test
    public void testSetRole() throws Exception {
        this.mockMvc
                .perform(get("/roles/set"))
                .andExpect(status().isOk())
                .andExpect(view().name("set-role"));
    }

    @Test
    public void testPostSetRole() throws Exception {
        this.mockMvc
                .perform(post("/roles/set")
                .param("username","admin")
                .param("roleName", "USER")
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
                .andExpectAll(view().name("redirect:/roles/set"));
    }

    @Test
    public void testPostSetRoleWithInvalidData() throws Exception {
        this.mockMvc
                .perform(post("/roles/set")
                        .param("username","")
                        .param("roleName", "USER")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpectAll(view().name("redirect:set"));
    }

}
