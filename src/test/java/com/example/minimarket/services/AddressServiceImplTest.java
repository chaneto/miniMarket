package com.example.minimarket.services;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.entities.CartEntity;
import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.model.services.AddressServiceModel;
import com.example.minimarket.model.views.AddressViewModel;
import com.example.minimarket.repositories.AddressRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;

    AddressEntity addressEntity1;
    AddressEntity addressEntity3;
    CourierEntity courierEntity;
    CartEntity cartEntity;

    @Before
    public void setup(){
        this.addressRepository.deleteAll();
        this.courierRepository.deleteAll();

        courierEntity = new CourierEntity();
        courierEntity.setName("DHL");
        courierEntity.setImageUrl("image");
        courierEntity.setRating(5);
        courierEntity.setShippingAmount(BigDecimal.valueOf(5.5));

        this.courierRepository.save(courierEntity);

        cartEntity = new CartEntity();

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

        addressEntity3 = new AddressEntity();
        addressEntity3.setStreetName("Hristo Botev");
        addressEntity3.setStreetNumber(23);
        addressEntity3.setCountry("Bulgaria");
        addressEntity3.setCity("Varna");
        addressEntity3.setZipCode("6060");
        addressEntity3.setDateTime(LocalDateTime.now());
        addressEntity3.setPaymentAmount(BigDecimal.valueOf(3));
        addressEntity3.setDelivered(false);
        addressEntity3.setCourier(courierEntity.getName());
    }

    @Test
    @WithMockUser
    public void testSaveAddress(){
        this.cartService.setCourier(courierEntity, this.userService.getCurrentCartId());
        Assert.assertEquals(0, this.addressRepository.count());
        this.addressService.save(this.mapper.map(addressEntity1, AddressServiceModel.class));
        this.addressService.save(this.mapper.map(addressEntity3, AddressServiceModel.class));
        Assert.assertEquals(2, this.addressRepository.count());
        this.cartService.setCourier(null, this.userService.getCurrentCartId());
        this.cartService.resetCart(this.userService.getCurrentCartId());
    }
    
    @Test
    @WithMockUser
    public void testDeleteAddressById(){
        this.addressRepository.deleteAll();
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(2, this.addressRepository.count());
        this.addressService.deleteById(this.addressRepository.findAll().get(0).getId());
        Assert.assertEquals(1, this.addressRepository.count());
    }

    @Test
    @WithMockUser
    public void testFindAllWithDateIsSmaller1Year(){
        addressEntity1.setDateTime(LocalDateTime.of(2020, 3, 3,15, 35 ));
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(1, this.addressService.findAllWithDateIsSmaller1Year().size());
    }

    @Test
    @WithMockUser
    public void testDeleteAllAddressesOlderThan1Year(){
        addressEntity1.setDateTime(LocalDateTime.of(2020, 3, 3,15, 35 ));
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(2, this.addressRepository.count());
        this.addressService.deleteAllAddressesOlderThan1Year();
        Assert.assertEquals(1, this.addressRepository.count());
    }

    @Test
    @WithMockUser
    public void testAddressCleaning(){
        addressEntity1.setDateTime(LocalDateTime.of(2020, 3, 3,15, 35 ));
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(2, this.addressRepository.count());
        this.addressService.addressCleaning();
        Assert.assertEquals(1, this.addressRepository.count());
    }

    @Test
    @WithMockUser
    public void updateFinallyPaymentAmount(){
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        this.cartService.setAddress(this.addressRepository
                .getById(this.addressRepository.findAll().get(0).getId()), this.userService.getCurrentCartId());
        this.addressService.updateFinallyPaymentAmount();
        this.cartService.resetCart( this.userService.getCurrentCartId());
    }

    @Test
    @WithMockUser
    public void getAllAddressesByCurrentUser(){
        Assert.assertEquals(0, this.addressService.getAllAddressesByCurrentUser().size());
    }

    @Test
    @WithMockUser
    public void getAllNotDeliveredAddresses(){
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(1, this.addressService.getAllNotDeliveredAddresses().size());
    }

    @Test
    @WithMockUser
    public void setOrdersToDelivered(){
        this.addressRepository.save(addressEntity1);
        this.addressRepository.save(addressEntity3);
        Assert.assertEquals(1, this.addressService.getAllNotDeliveredAddresses().size());
        this.addressService.setOrdersToDelivered(Long.valueOf(2));
        Assert.assertEquals(0, this.addressService.getAllNotDeliveredAddresses().size());
    }

    @Test
    public  void conversionToListViewModel(){
        List<AddressEntity> addresses = List.of(addressEntity1, addressEntity3);
        List<AddressViewModel> views = this.addressService.conversionToListViewModel(addresses);
        Assert.assertEquals(addresses.get(0).getStreetName(), views.get(0).getStreetName());

    }

    @Test
    @WithMockUser
    public void findAllByUserId(){
        Assert.assertEquals(0, this.addressService.findAllByUserId(this.userService.getCurrentUser().getId()).size());
    }

}
