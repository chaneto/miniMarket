package com.example.minimarket.services;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.services.AddressServiceModel;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.AddressRepository;
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

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AddressServiceImplTest {

    @Autowired
    private AddressService addressService;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserService userService;

    AddressEntity addressEntity1;
    AddressEntity addressEntity3;

    @Before
    public void setup(){
        this.addressRepository.deleteAll();

        addressEntity1 = new AddressEntity();
        addressEntity1.setStreetName("Shipka");
        addressEntity1.setStreetNumber(5);
        addressEntity1.setCountry("Bulgaria");
        addressEntity1.setCity("Plovdiv");
        addressEntity1.setZipCode("6000");
        addressEntity1.setDateTime(LocalDateTime.now());


        addressEntity3 = new AddressEntity();
        addressEntity3.setStreetName("Hristo Botev");
        addressEntity3.setStreetNumber(23);
        addressEntity3.setCountry("Bulgaria");
        addressEntity3.setCity("Varna");
        addressEntity3.setZipCode("6060");
        addressEntity3.setDateTime(LocalDateTime.now());

    }

    @Test
    public void testSaveAddress(){
        authenticate();
        Assert.assertEquals(0, this.addressRepository.count());
        this.addressService.save(this.mapper.map(addressEntity1, AddressServiceModel.class));
        this.addressService.save(this.mapper.map(addressEntity3, AddressServiceModel.class));
        Assert.assertEquals(2, this.addressRepository.count());
    }
    
    @Test
    public void testDeleteAddressById(){
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(2, this.addressRepository.count());
        this.addressService.deleteById(Long.valueOf(1));
        Assert.assertEquals(1, this.addressRepository.count());
    }

    @Test
    public void testFindAllWithDateIsSmaller6Months(){
        addressEntity1.setDateTime(LocalDateTime.of(2021, 3, 3,15, 35 ));
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(1, this.addressService.findAllWithDateIsSmaller6Months().size());
    }

    @Test
    public void testDeleteAllAddressesOlderThan6Months(){
        addressEntity1.setDateTime(LocalDateTime.of(2021, 3, 3,15, 35 ));
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(2, this.addressRepository.count());
        this.addressService.deleteAllAddressesOlderThan6Months();
        Assert.assertEquals(1, this.addressRepository.count());
    }

    @Test
    public void testAddressCleaning(){
        addressEntity1.setDateTime(LocalDateTime.of(2021, 3, 3,15, 35 ));
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(2, this.addressRepository.count());
        this.addressService.addressCleaning();
        Assert.assertEquals(1, this.addressRepository.count());
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
