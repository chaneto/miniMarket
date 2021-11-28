package com.example.minimarket.services;

import com.example.minimarket.model.entities.CartEntity;
import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.model.entities.OrderEntity;
import com.example.minimarket.model.services.CourierServiceModel;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.model.views.CourierViewModel;
import com.example.minimarket.repositories.CartRepository;
import com.example.minimarket.repositories.CourierRepository;
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
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CourierServiceImplTest {

    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    CourierService courierService;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserService userService;
    @Autowired
    ModelMapper mapper;

    CourierEntity courierEntity1;
    CourierEntity courierEntity2;
    CourierEntity courierEntity3;
    CartEntity cartEntity;

    @Before
    public void setup(){
        for(CourierEntity courier: this.courierRepository.findAll()){
            if(!courier.getName().equals("ekont")){
            this.courierRepository.deleteByName(courier.getName());
            }
        }
        courierEntity1 = new CourierEntity();
        courierEntity1.setName("dhl");
        courierEntity1.setRating(5);
        courierEntity1.setShippingAmount(BigDecimal.valueOf(5.50));
        courierEntity1.setImageUrl("image");

        courierEntity2 = new CourierEntity();
        courierEntity2.setName("ekont");
        courierEntity2.setRating(5);
        courierEntity2.setShippingAmount(BigDecimal.valueOf(5.50));
        courierEntity2.setImageUrl("image");
        if(this.courierRepository.count() == 0){
        this.courierRepository.save(courierEntity2);
        }

        courierEntity3 = new CourierEntity();
        courierEntity3.setName("speedy");
        courierEntity3.setRating(4);
        courierEntity3.setShippingAmount(BigDecimal.valueOf(6.50));
        courierEntity3.setImageUrl("image");


        cartEntity = new CartEntity();
        cartEntity.setTotalPrice(BigDecimal.valueOf(0));
        cartEntity.setCourier(null);
        cartEntity.setAddress(null);
        List<OrderEntity> orders = new ArrayList<>();
        cartEntity.setOrders(orders);
        this.cartRepository.save(cartEntity);
    }

    @Test
    public void testFindByName(){
        this.courierRepository.save(courierEntity1);
        String courierName = courierEntity1.getName();
        CourierServiceModel courier = this.courierService.findByName(courierName);
        Assert.assertEquals(courierName, courier.getName());
    }

    @Test
    public void testFindByNonExistingName(){
        this.courierRepository.save(courierEntity1);
        CourierServiceModel courier = this.courierService.findByName("Ekont");
        Assert.assertNull(courier);
    }

    @Test
    public void testSaveCourier(){
        Assert.assertEquals(1, this.courierRepository.count());
        this.courierService.saveCourier(this.mapper.map(courierEntity1, CourierServiceModel.class));
        this.courierService.saveCourier(this.mapper.map(courierEntity3, CourierServiceModel.class));
        Assert.assertEquals(3, this.courierRepository.count());
    }

    @Test
    public void testFindAll(){
        this.courierRepository.save(courierEntity1);
        this.courierRepository.save(courierEntity3);
        List<CourierViewModel> couriers = this.courierService.findAll();
        Assert.assertEquals(3, couriers.size());
    }

    @Test
    public void testSetCourierCart(){
        authenticate();
        Assert.assertNull(cartEntity.getCourier());
        this.courierService.setCourierCart(courierEntity2.getName());
        Assert.assertEquals(courierEntity2.getName(), this.userService.getCurrentCart().getCourier().getName());
    }

    @Test
    public void testDeleteCourierByName(){
        this.courierRepository.save(courierEntity1);
        this.courierRepository.save(courierEntity3);
        Assert.assertEquals(3, this.courierRepository.count());
        this.courierService.deleteByName(courierEntity1.getName());
        Assert.assertEquals(2, this.courierRepository.count());
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
