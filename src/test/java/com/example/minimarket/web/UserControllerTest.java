package com.example.minimarket.web;

import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.entities.UserRoleEntity;
import com.example.minimarket.model.enums.UserRole;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.CartRepository;
import com.example.minimarket.repositories.UserRoleRepository;
import com.example.minimarket.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.minimarket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    UserEntity user1;
    UserEntity user3;
    UserRoleEntity userRoleEntity1;

    @Before
    public void setup(){

        this.cartRepository.deleteAll();
        this.userRepository.deleteAll();
        this.userRoleRepository.deleteAll();

        userRoleEntity1 = new UserRoleEntity();
        userRoleEntity1.setUserRole(UserRole.ADMIN);
        this.userRoleRepository.save(userRoleEntity1);

        user1 = new UserEntity();
        user1.setUsername("admin");
        user1.setFirstName("Admin");
        user1.setLastName("Adminov");
        user1.setEmail("admin_80@abv.bg");
        user1.setPassword("12345");
        user1.setPhoneNumber("123456789");
        user1.setRole(userRoleEntity1);

        user3 = new UserEntity();
        user3.setUsername("petko");
        user3.setFirstName("Petko");
        user3.setLastName("Petkov");
        user3.setEmail("petko_80@abv.bg");
        user3.setPassword("12345");
        user3.setPhoneNumber("123456789");

    }

    @Test
    @WithMockUser
    public void testLoginCorrectReturnView() throws Exception {
        this.mockMvc
                .perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser
    public void testRegisterCorrectReturnView() throws Exception {
        this.mockMvc
                .perform(get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("/register"));
    }
    @Test
    public void registerCorrectly() throws Exception {
        this.userRepository.saveAndFlush(user1);
        this.userRepository.saveAndFlush(user3);
        Assert.assertEquals(2, this.userRepository.count());
    }

    @Test
    @WithMockUser
    public void testRegisterCorrectReturnViewLogin() throws Exception {
        this.userRepository.save(user1);
        Assert.assertEquals(1, this.userRepository.count());
        this.mockMvc
                .perform(post("/users/register")
                        .param("username", "pencho")
                        .param("firstName", "Pencho")
                        .param("lastName", "Penchev")
                        .param("email", "pencho@abv.bg")
                        .param("password", "12345")
                        .param("confirmPassword", "12345")
                        .param("phoneNumber", "123456789").
                 with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:login"));
        Assert.assertEquals(2, this.userRepository.count());

    }

    @Test
    @WithMockUser
    public void testRegisterWithNotMatchesPasswords() throws Exception {
        this.userRepository.save(user1);
        Assert.assertEquals(1, this.userRepository.count());
        this.mockMvc
                .perform(post("/users/register")
                        .param("username", "pencho")
                        .param("firstName", "Pencho")
                        .param("lastName", "Penchev")
                        .param("email", "pencho@abv.bg")
                        .param("password", "12345")
                        .param("confirmPassword", "123456789")
                        .param("phoneNumber", "123456789").
                                with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:register"));
        Assert.assertEquals(1, this.userRepository.count());

    }

    @Test
    @WithMockUser
    public void testRegisterWithExistingUsername() throws Exception {
        this.userRepository.save(user1);
        Assert.assertEquals(1, this.userRepository.count());
        this.mockMvc
                .perform(post("/users/register")
                        .param("username", "admin")
                        .param("firstName", "Pencho")
                        .param("lastName", "Penchev")
                        .param("email", "pencho@abv.bg")
                        .param("password", "12345")
                        .param("confirmPassword", "12345")
                        .param("phoneNumber", "123456789").
                                with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:register"));
        Assert.assertEquals(1, this.userRepository.count());

    }

    @Test
    @WithMockUser
    public void testRegisterWithExistingEmail() throws Exception {
        this.userRepository.save(user1);
        Assert.assertEquals(1, this.userRepository.count());
        this.mockMvc
                .perform(post("/users/register")
                        .param("username", "pencho")
                        .param("firstName", "Pencho")
                        .param("lastName", "Penchev")
                        .param("email", "admin_80@abv.bg")
                        .param("password", "12345")
                        .param("confirmPassword", "123456789")
                        .param("phoneNumber", "123456789").
                                with(csrf())).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:register"));
        Assert.assertEquals(1, this.userRepository.count());

    }

    @Test
    @WithMockUser
    public void testRegisterWithIncorrectData() throws Exception {
        this.userRepository.save(user1);
        Assert.assertEquals(1, this.userRepository.count());
        this.mockMvc
                .perform(post("/users/register")
                        .param("username", "p")
                        .param("firstName", "Pencho")
                        .param("lastName", "P")
                        .param("email", "admin_80@abv.bg")
                        .param("password", "12345")
                        .param("confirmPassword", "123456789")
                        .param("phoneNumber", "123456789").
                                with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:register"));
        Assert.assertEquals(1, this.userRepository.count());

    }

    @Test
    @WithMockUser
    public void testFaledLogin() throws Exception {
        this.userRepository.save(user1);
        this.mockMvc
                .perform(post("/users/login-error")
                        .param("username", "ivan")
                        .param("password", "11111")
                .with(csrf()))
                 .andExpect(status().is3xxRedirection());
               // .andExpect(view().name("redirect:login"));

    }

    @Test
    @WithMockUser
    public void testLoginWithInvalidDataCorrect() throws Exception {
        this.mockMvc
                .perform(post("/users/login")
                        .param("username", "")
                        .param("password", "1234555")
                .with(csrf())).
                andExpect(status().is2xxSuccessful());
               // .andExpect(view().name("redirect:login"));

    }

    @Test
    @WithMockUser
    public void testLoginWithCorrectData() throws Exception {
        this.mockMvc
                .perform(post("/users/login")
                        .param("username", "admin")
                        .param("password", "12345")
                .with(csrf()))
                .andExpect(status().is2xxSuccessful());
        //.andExpect(view().name("redirect:/"));
    }

    @Test
    @WithMockUser
    public void testProfile() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/users/profile"))
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("currentUser"));
    }


    @Test
    @WithMockUser
    public void testChangePassword() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/users/changePassword"))
                .andExpect(view().name("change-password"));
    }

    @Test
    @WithMockUser
    public void testChangePasswordConfirm() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/users/changePassword")
                        .param("password", "12345")
                        .param("confirmPassword", "12345")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/users/profile"));
    }

    @Test
    @WithMockUser
    public void testChangePasswordConfirmWithNotMatchesField() throws Exception {
        this.mockMvc
                .perform(post("/users/changePassword")
                        .param("password", "12345")
                        .param("confirmPassword", "123453")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:changePassword"));
    }

    @Test
    @WithMockUser
    public void testChangePasswordConfirmWithInvalidPassword() throws Exception {
        this.mockMvc
                .perform(post("/users/changePassword")
                        .param("password", "1")
                        .param("confirmPassword", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:changePassword"));
    }

    @Test
    @WithMockUser
    public void testChangeEmail() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/users/changeEmail"))
                .andExpect(view().name("change-email"));
    }

    @Test
    @WithMockUser
    public void testChangeEmailConfirm() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/users/changeEmail")
                        .param("email", "boncho@abv.bg")
                        .param("confirmEmail", "boncho@abv.bg")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/profile"));
    }

    @Test
    @WithMockUser
    public void testChangeEmailConfirmWithInvalidEmail() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/users/changeEmail")
                        .param("email", "b")
                        .param("confirmEmail", "b")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:changeEmail"));
    }

    @Test
    @WithMockUser
    public void testChangeEmailConfirmWithNotMatchesField() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/users/changeEmail")
                        .param("email", "boncho@abv.bg")
                        .param("confirmEmail", "petar@abv.bg")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:changeEmail"));
    }

    @Test
    @WithMockUser
    public void testChangeEmailConfirmWithExistingEmail() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/users/changeEmail")
                        .param("email", "admin_80@abv.bg")
                        .param("confirmEmail", "admin_80@abv.bg")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:changeEmail"));
    }

    @Test
    @WithMockUser
    public void testAllusers() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/users/all"))
                .andExpect(view().name("all-users"))
        .andExpect(model().attributeExists("allUsers"));
    }

    @Test
    @WithMockUser
    public void testDeleteUser() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/users/delete/admin"))
                .andExpect(view().name("redirect:/users/all"));
    }

    @Test
    @WithMockUser
    public void testDeleteUserConfirm() throws Exception {
        this.mockMvc
                .perform(post("/users/delete")
                        .param("username", "user")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/all"));
    }

    @Test
    @WithMockUser
    public void testDeleteUserConfirmWithInvalidUsername() throws Exception {
        this.mockMvc
                .perform(post("/users/delete")
                        .param("username", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/all"));
    }

    @Test
    @WithMockUser
    public void testDeleteUserConfirmWithINotExistingUsername() throws Exception {
        this.mockMvc
                .perform(post("/users/delete")
                        .param("username", "Ivan")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users/all"));
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
