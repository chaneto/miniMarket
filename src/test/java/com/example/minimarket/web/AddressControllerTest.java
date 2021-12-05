package com.example.minimarket.web;

import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.UserRepository;
import com.example.minimarket.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Test
    @WithMockUser
    public void testAddAddressView() throws Exception {
        this.mockMvc
                .perform(get("/addresses/add"))
                .andExpect(view().name("add-address"));
    }

    @Test
    @WithMockUser
    public void testList() throws Exception {
        this.mockMvc
                .perform(get("/addresses/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("addresses-list"));
    }

    @Test
    @WithMockUser
    public void testFindAllNotDelivered() throws Exception {
        this.userService.setUserRole(this.userService.getCurrentUser().getUsername(), "ADMIN");
        this.mockMvc
                .perform(get("/addresses/findAllNotDelivered"))
                .andExpect(model().attributeExists("isNotDeliveredOrders"))
                .andExpect(view().name("all-is-not-delivered-orders"));
    }

    @Test
    @WithMockUser
    public void testSetToDelivered() throws Exception {
        this.mockMvc
                .perform(get("/addresses/setToDelivered/1"))
                .andExpect(view().name("redirect:/addresses/findAllNotDelivered"));
    }

    @Test
    @WithMockUser
    public void testAddAddressConfirmWithInvalidDataView() throws Exception {
        this.mockMvc
                .perform(post("/addresses/add")
                        .param("streetName", "")
                        .param("streetNumber", "5")
                        .param("apartmentNumber", "5")
                        .param("city", "Plovdiv")
                        .param("country", "Bulgaria")
                        .param("zipCode", "6000")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
    }


}