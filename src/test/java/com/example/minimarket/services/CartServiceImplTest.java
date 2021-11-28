package com.example.minimarket.services;

import com.example.minimarket.model.entities.AddressEntity;
import com.example.minimarket.model.entities.CartEntity;
import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.model.views.CartViewModel;
import com.example.minimarket.repositories.AddressRepository;
import com.example.minimarket.repositories.CartRepository;
import com.example.minimarket.repositories.ProductRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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


    CartEntity cartEntity;
    AddressEntity addressEntity;
    ProductEntity productEntity;

    @Before
    public void setup(){
        this.cartRepository.deleteAll();

        cartEntity = new CartEntity();
        cartEntity.setTotalPrice(BigDecimal.valueOf(0));
        cartEntity.setAddress(null);
    }

    @Test
    public void testSetTotalPrice(){
        this.cartRepository.save(cartEntity);
        BigDecimal newPrice = BigDecimal.valueOf(33);
        this.cartService.setTotalPrice(newPrice, Long.valueOf(1));
        Assert.assertEquals(newPrice.stripTrailingZeros(), this.cartRepository.getCartById(Long.valueOf(1)).getTotalPrice().stripTrailingZeros());
    }

    @Test
    public void testGetCartById(){
        authenticate();
        this.cartRepository.save(cartEntity);
        CartViewModel cart = this.cartService.getCartById(Long.valueOf(1));
        Assert.assertEquals(cart.getTotalPrice().stripTrailingZeros(), cartEntity.getTotalPrice().stripTrailingZeros());
    }

    @Test
    public void testGetCartByNonExistingId(){
        authenticate();
        this.cartRepository.save(cartEntity);
        CartViewModel cart = this.cartService.getCartById(Long.valueOf(99));
        Assert.assertNull(cart);
    }

    @Test
    public void testSetAddress(){
        authenticate();
        Assert.assertNull(this.cartRepository.getCartById(Long.valueOf(1)).getAddress());
        this.cartService.setAddress(getAddress(), Long.valueOf(1));
        Assert.assertNotNull(this.cartRepository.getCartById(Long.valueOf(1)).getAddress());
    }

    @Test
    public void testResetCart(){
        authenticate();
        this.cartService.setTotalPrice(BigDecimal.valueOf(33), Long.valueOf(1));
        Assert.assertEquals(BigDecimal.valueOf(33).stripTrailingZeros(), this.cartRepository.getCartById(Long.valueOf(1)).getTotalPrice().stripTrailingZeros());
        this.cartService.resetCart(Long.valueOf(1));
        Assert.assertEquals(BigDecimal.valueOf(0).stripTrailingZeros(), this.cartRepository.getCartById(Long.valueOf(1)).getTotalPrice().stripTrailingZeros());
    }

    @Test
    public void testAddProductToCart(){
        this.productRepository.save(getProduct());
        this.cartRepository.save(cartEntity);
        Assert.assertEquals(BigDecimal.valueOf(0).stripTrailingZeros(), this.cartRepository.getCartById(Long.valueOf(1)).getTotalPrice().stripTrailingZeros());
        this.cartService.addProductToCart(getProduct().getName(), BigDecimal.valueOf(1), Long.valueOf(1));
        Assert.assertEquals(getProduct().getPrice().stripTrailingZeros(), this.cartRepository.getCartById(Long.valueOf(1)).getTotalPrice().stripTrailingZeros());
    }

    @Test
    public void testDeleteCartById(){
        this.cartRepository.save(cartEntity);
        this.cartService.deleteCartById(Long.valueOf(1));
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
        addressEntity = new AddressEntity();
        addressEntity.setStreetName("Shipka");
        addressEntity.setStreetNumber(5);
        addressEntity.setCountry("Bulgaria");
        addressEntity.setCity("Plovdiv");
        addressEntity.setZipCode("6000");
        addressEntity.setDateTime(LocalDateTime.now());
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
//        productEntity.setCategory(categoryEntity);
//        productEntity.setBrand(brandEntity);
        return productEntity;
    }
}
