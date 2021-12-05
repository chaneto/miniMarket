package com.example.minimarket.services;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.model.views.CartViewModel;
import com.example.minimarket.repositories.*;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CartServiceImplTest {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CourierRepository courierRepository;
    @Autowired
    ModelMapper mapper;

    CartEntity cartEntity;
    AddressEntity addressEntity;
    ProductEntity productEntity;

    @Before
    public void setup(){
        this.cartRepository.deleteAll();

        cartEntity = new CartEntity();
        cartEntity.setTotalPrice(BigDecimal.valueOf(0));
        cartEntity.setAddress(null);
        this.cartRepository.save(cartEntity);
    }

    @Test
    @WithMockUser
    public void testSetTotalPrice(){
        BigDecimal newPrice = BigDecimal.valueOf(33);
        this.cartService.setTotalPrice(newPrice, this.cartRepository.findAll().get(0).getId());
        Assert.assertEquals(newPrice.stripTrailingZeros(), this.cartRepository.getCartById(this.cartRepository.findAll().get(0).getId()).getTotalPrice().stripTrailingZeros());
    }

    @Test
    @WithMockUser
    public void testGetCartById(){
        this.cartRepository.deleteAll();
       authenticate();
       this.cartRepository.save(cartEntity);
        CartViewModel cart = this.cartService.getCartById( this.cartRepository.findAll().get(0).getId());
        Assert.assertEquals(cart.getTotalPrice().stripTrailingZeros(), cartEntity.getTotalPrice().stripTrailingZeros());
        this.userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void updateTotalPrice(){
        this.cartRepository.deleteAll();
        cartEntity.setCourier(this.courierRepository.findByName("DHL"));
        this.cartRepository.save(cartEntity);
        this.cartService.updateTotalPrice(cartEntity);
        Assert.assertEquals(BigDecimal.valueOf(0).stripTrailingZeros(),
                this.cartRepository.findAll().get(0).getTotalPrice().stripTrailingZeros());
    }

    @Test
    public void findByCourierIsNotNull(){
        Assert.assertEquals(0, this.cartService.findByCourierIsNotNull().size());
    }
    
    @Test
    
    public void cartWithCourierWithUndeliveredOrder(){
        Assert.assertFalse(this.cartService.cartWithCourierWithUndeliveredOrder("Speedy"));
        getCourier();
        cartEntity.setCourier(this.courierRepository.findByName("DHL"));
        this.cartRepository.save(cartEntity);
        Assert.assertTrue(this.cartService.cartWithCourierWithUndeliveredOrder("DHL"));
    }

    @Test
    @WithMockUser
    public void testGetCartByNonExistingId(){
        CartViewModel cart = this.cartService.getCartById(Long.valueOf(99));
        Assert.assertNull(cart);
    }

    @Test
    @WithMockUser
    public void testSetAddress(){
        Assert.assertNull(this.cartRepository.getCartById( this.cartRepository.findAll().get(0).getId()).getAddress());
        this.cartService.setAddress(getAddress(),  this.cartRepository.findAll().get(0).getId());
        Assert.assertNotNull(this.cartRepository.getCartById( this.cartRepository.findAll().get(0).getId()).getAddress());
    }

    @Test
    public void testCartWithOrderNotDeliveredToAddress(){
        this.cartService.resetCart( this.cartRepository.findAll().get(0).getId());
        AddressEntity addressEntity = new AddressEntity();
        Assert.assertFalse(this.cartService.cartWithOrderNotDeliveredToAddress(addressEntity));
        this.cartService.setAddress(getAddress(),  this.cartRepository.findAll().get(0).getId());
        Assert.assertTrue(this.cartService.cartWithOrderNotDeliveredToAddress(this.addressRepository.findAll().get(0)));
    }

    @Test
    @WithMockUser
    public void testResetCart(){
        this.cartService.setTotalPrice(BigDecimal.valueOf(33),  this.cartRepository.findAll().get(0).getId());
        Assert.assertEquals(BigDecimal.valueOf(33).stripTrailingZeros(), this.cartRepository.getCartById( this.cartRepository.findAll().get(0).getId()).getTotalPrice().stripTrailingZeros());
        this.cartService.resetCart( this.cartRepository.findAll().get(0).getId());
        Assert.assertEquals(BigDecimal.valueOf(0).stripTrailingZeros(), this.cartRepository.getCartById( this.cartRepository.findAll().get(0).getId()).getTotalPrice().stripTrailingZeros());
    }

    @Test
    @WithMockUser
    public void testAddProductToCart(){
        this.cartRepository.deleteAll();
        authenticate();
        this.cartRepository.save(cartEntity);
        this.productRepository.save(getProduct());
        Assert.assertEquals(BigDecimal.valueOf(0).stripTrailingZeros(), this.cartRepository.getCartById( this.cartRepository.findAll().get(0).getId()).getTotalPrice().stripTrailingZeros());
        this.cartService.addProductToCart(getProduct().getName(), BigDecimal.valueOf(1),  this.cartRepository.findAll().get(0).getId());
        Assert.assertEquals(getProduct().getPrice().stripTrailingZeros(), this.cartRepository.getCartById( this.cartRepository.findAll().get(0).getId()).getTotalPrice().stripTrailingZeros());
        this.userRepository.deleteAll();
    }

    @Test
    public void testClearCartById(){
        this.cartService.clearCartById( this.cartRepository.findAll().get(0).getId());
    }

    @Test
    public void testDeleteCartById(){
        Assert.assertEquals(1, this.cartRepository.count());
        this.cartService.deleteCartById( this.cartRepository.findAll().get(0).getId());
        Assert.assertEquals(0, this.cartRepository.count());
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

    public AddressEntity getAddress(){
        this.addressRepository.deleteAll();
        addressEntity = new AddressEntity();
        addressEntity.setStreetName("Shipka");
        addressEntity.setStreetNumber(5);
        addressEntity.setCountry("Bulgaria");
        addressEntity.setCity("Plovdiv");
        addressEntity.setZipCode("6000");
        addressEntity.setDateTime(LocalDateTime.now());
        addressEntity.setPaymentAmount(BigDecimal.valueOf(30));
        addressEntity.setDelivered(true);
        this.addressRepository.save(addressEntity);
        return addressEntity;
    }

    public ProductEntity getProduct() {
        productEntity = new ProductEntity();
        productEntity.setName("case");
        productEntity.setPrice(BigDecimal.valueOf(10));
        productEntity.setQuantity(BigDecimal.valueOf(100));
        productEntity.setImage("image");
        productEntity.setDescription("The best product...");
        productEntity.setOnPromotion(true);
        productEntity.setDiscountRate(BigDecimal.valueOf(30));
        productEntity.setPromotionPrice(BigDecimal.valueOf(10));
//        productEntity.setCategory(categoryEntity);
//        productEntity.setBrand(brandEntity);
        return productEntity;
    }

    public CourierEntity getCourier(){
        this.courierRepository.deleteAll();
        CourierEntity courierEntity = new CourierEntity();
        courierEntity.setName("DHL");
        courierEntity.setRating(5);
        courierEntity.setShippingAmount(BigDecimal.valueOf(5));
        courierEntity.setImageUrl("img");
        this.courierRepository.save(courierEntity);
        return courierEntity;
    }

}
