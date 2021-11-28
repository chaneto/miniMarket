package com.example.minimarket.services;

import com.example.minimarket.model.entities.CartEntity;
import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.entities.UserRoleEntity;
import com.example.minimarket.model.enums.UserRole;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.model.views.OrderViewModel;
import com.example.minimarket.repositories.CartRepository;
import com.example.minimarket.repositories.UserRepository;
import com.example.minimarket.repositories.UserRoleRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    UserEntity user1;
    UserEntity user3;
    UserRoleEntity userRoleEntity1;
    UserRoleEntity userRoleEntity3;
    CartEntity cartEntity1;
    CartEntity cartEntity3;

    @Before
    public void setup(){

        this.userRepository.deleteAll();
        this.cartRepository.deleteAll();
        this.userRoleRepository.deleteAll();

            userRoleEntity1 = new UserRoleEntity();
            userRoleEntity3 = new UserRoleEntity();
            userRoleEntity1.setUserRole(UserRole.ADMIN);
            userRoleEntity3.setUserRole(UserRole.USER);
            this.userRoleRepository.save(userRoleEntity1);
            this.userRoleRepository.save(userRoleEntity3);
            cartEntity1 = new CartEntity();
            cartEntity1.setTotalPrice(BigDecimal.valueOf(0));
            cartEntity3 = new CartEntity();
            cartEntity3.setTotalPrice(BigDecimal.valueOf(0));
            this.cartRepository.save(cartEntity1);
            this.cartRepository.save(cartEntity3);

        user1 = new UserEntity();
        user1.setUsername("admin");
        user1.setFirstName("Admin");
        user1.setLastName("Adminov");
        user1.setEmail("admin_80@abv.bg");
        user1.setPassword("12345");
        user1.setPhoneNumber("123456789");
        user1.setRole(userRoleEntity1);
        user1.setCart(cartEntity1);

        user3 = new UserEntity();
        user3.setUsername("petko");
        user3.setFirstName("Petko");
        user3.setLastName("Petkov");
        user3.setEmail("petko_80@abv.bg");
        user3.setPassword("12345");
        user3.setPhoneNumber("123456789");
        user1.setRole(userRoleEntity3);
        user1.setCart(cartEntity3);

        this.userRepository.save(user1);
        this.userRepository.save(user3);
        cartEntity1.setUser(user1);
        cartEntity3.setUser(user3);
    }

    @Test
    public void testFindByUsername(){
        String username = user1.getUsername();
        UserRegisterServiceModel userRegisterServiceModel = this.userService.findByUsername(username);
        Assert.assertEquals(username, userRegisterServiceModel.getUsername());
        Assert.assertEquals("12345", userRegisterServiceModel.getPassword());
    }

    @Test
    public void testFindByUsernameEntity(){
        UserEntity userEntity = this.userService.findByUsernameEntity(user1.getUsername());
        Assert.assertEquals("admin", userEntity.getUsername());
        Assert.assertEquals("12345", userEntity.getPassword());
    }

    @Test
    public void testFindByUsernameAndEmail(){
        UserRegisterServiceModel userRegisterServiceModel = this.userService.findByUsernameAndEmail(user1.getUsername(), user1.getEmail());
        Assert.assertEquals("admin", userRegisterServiceModel.getUsername());
        Assert.assertEquals("12345", userRegisterServiceModel.getPassword());
    }

    @Test
    public void testUserWithUsernameIsExists(){
        Assert.assertTrue(this.userService.userWithUsernameIsExists("admin"));
    }

    @Test
    public void testUserWithUsernameIsNotExists(){
        Assert.assertFalse(this.userService.userWithUsernameIsExists("ivan"));
    }

    @Test
    public void testUserWithEmailIsExists(){
        Assert.assertTrue(this.userService.userWithEmailIsExists("admin_80@abv.bg"));

    }

    @Test
    public void testUserWithEmailIsNotExists(){
        Assert.assertFalse(this.userService.userWithEmailIsExists("ivan_@abv.bg"));
    }

    @Test
    public void testRegisterUser(){
        this.userRepository.deleteAll();
        this.cartRepository.deleteAll();
        UserRegisterServiceModel userRegisterServiceModel = this.mapper.map(user1, UserRegisterServiceModel.class);
        Assert.assertEquals(0, this.userRepository.count());
        this.userService.registerUser(userRegisterServiceModel);
        Assert.assertEquals("ADMIN", this.userRepository.findAll().get(0).getRole().getUserRole().name());
        Assert.assertEquals(1, this.userRepository.count());
        UserRegisterServiceModel userRegisterServiceModel1 = this.mapper.map(user3, UserRegisterServiceModel.class);
        this.userService.registerUser(userRegisterServiceModel1);
        Assert.assertEquals("USER", this.userRepository.findAll().get(1).getRole().getUserRole().name());
        Assert.assertEquals(2, this.userRepository.count());
    }

    @Test
    public void testAuthenticate(){
      authenticate();
    }

    @Test
    public void testGetCountAllUserOrders(){
        authenticate();
        Assert.assertEquals(0, this.userService.getCountAllUserOrders());
    }

    @Test
    public void testGetTotalPriceForAllOrder(){
        authenticate();
        BigDecimal current = new BigDecimal("0");
        Assert.assertEquals(current.stripTrailingZeros(), this.userService.getTotalPriceForAllOrders().stripTrailingZeros());
    }

    @Test
    public void testFindAllUsername(){
        List<String> usernames = this.userService.findAllUsername();
        Assert.assertEquals(2, usernames.size());
     }

     @Test
     public void testSetUserRole(){
        authenticate();
        this.userService.setUserRole(user1.getUsername(), "USER");
        Assert.assertEquals(user1.getRole().getUserRole().name(), "USER");
     }

     @Test
     public void getCurrentCart(){
        authenticate();
        Assert.assertEquals(cartEntity1.getUser().getUsername(), this.userService.getCurrentCart().getUser());
     }

     @Test
     public void testGetAllUserOrderByIsPaid(){
    List<OrderViewModel> orders = this.userService.getAllUserOrderByIsPaid(false, Long.valueOf(1));
    Assert.assertEquals(0, orders.size());
    }



    public void authenticate(){
        UserLoginServiceModel userLoginServiceModel = new UserLoginServiceModel();
        userLoginServiceModel.setUsername("admin");
        userLoginServiceModel.setPassword("12345");
        this.userService.authenticate(userLoginServiceModel);
    }
}
