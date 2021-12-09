package com.example.minimarket.services;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.model.views.OrderViewModel;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OrderServiceImplTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CourierRepository courierRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    private ModelMapper mapper;

    ProductEntity productEntity1;
    ProductEntity productEntity3;
    CategoryEntity categoryEntity;
    BrandEntity brandEntity;
    OrderEntity orderEntity;
    CartEntity cartEntity;
    CourierEntity courierEntity;
    AddressEntity addressEntity;

    @Before
    public void setup(){
        this.orderRepository.deleteAll();
        this.brandRepository.deleteAll();
        this.categoryRepository.deleteAll();
        this.productRepository.deleteAll();
        this.cartRepository.deleteAll();
        this.courierRepository.deleteAll();
        this.addressRepository.deleteAll();
        this.userRepository.deleteAll();

        brandEntity = new BrandEntity();
        brandEntity.setName("Nokia");
        brandEntity.setImage("image");
        this.brandRepository.save(brandEntity);

        categoryEntity = new CategoryEntity();
        categoryEntity.setName("cases");
        categoryEntity.setDescription("Cases for all smartphones...");
        categoryEntity.setImage("image");
        this.categoryRepository.save(categoryEntity);

        productEntity1 = new ProductEntity();
        productEntity1.setName("case");
        productEntity1.setPrice(BigDecimal.valueOf(10));
        productEntity1.setQuantity(BigDecimal.valueOf(100));
        productEntity1.setImage("image");
        productEntity1.setDescription("The best product...");
        productEntity1.setOnPromotion(true);
        productEntity1.setCategory(categoryEntity);
        productEntity1.setBrand(brandEntity);
        productEntity1.setDiscountRate(BigDecimal.valueOf(20));
        productEntity1.setPromotionPrice(BigDecimal.valueOf(11));

        productEntity3 = new ProductEntity();
        productEntity3.setName("TPU");
        productEntity3.setPrice(BigDecimal.valueOf(30));
        productEntity3.setQuantity(BigDecimal.valueOf(300));
        productEntity3.setImage("image");
        productEntity3.setDescription("The best product...");
        productEntity3.setOnPromotion(false);
        productEntity3.setCategory(categoryEntity);
        productEntity3.setBrand(brandEntity);
        productEntity3.setDiscountRate(BigDecimal.valueOf(20));
        productEntity3.setPromotionPrice(BigDecimal.valueOf(11));

        this.productRepository.save(productEntity1);
        this.productRepository.save(productEntity3);


        courierEntity = new CourierEntity();
        courierEntity.setName("dhl");
        courierEntity.setRating(5);
        courierEntity.setShippingAmount(BigDecimal.valueOf(5.50));
        courierEntity.setImageUrl("image");
        this.courierRepository.save(courierEntity);

        addressEntity = new AddressEntity();
        addressEntity.setStreetName("Shipka");
        addressEntity.setStreetNumber(5);
        addressEntity.setCountry("Bulgaria");
        addressEntity.setCity("Plovdiv");
        addressEntity.setZipCode("6000");
        addressEntity.setDateTime(LocalDateTime.now());
        addressEntity.setPaymentAmount(BigDecimal.valueOf(33));
        addressEntity.setDelivered(true);
        this.addressRepository.save(addressEntity);

        authenticate();

        cartEntity = new CartEntity();
        cartEntity.setTotalPrice(BigDecimal.valueOf(0));
        cartEntity.setCourier(courierEntity);
        cartEntity.setAddress(addressEntity);
        List<OrderEntity> orders = new ArrayList<>();
        cartEntity.setOrders(orders);
        this.cartRepository.save(cartEntity);

        orderEntity = new OrderEntity();
        orderEntity.setDateTime(LocalDateTime.now());
        orderEntity.setTotalPrice(BigDecimal.valueOf(40));
        orderEntity.setProduct(productEntity1);
        orderEntity.setPaid(false);
        orderEntity.setProductCount(1);
        orderEntity.setCourier(courierEntity);
        orderEntity.setAddress(addressEntity);
        orderEntity.setCart(cartEntity);
        orderEntity.setUsername("Petko");
        orderEntity.setOrdered(true);
        orderEntity.setDelivered(false);
        this.orderRepository.save(orderEntity);

    }

    @Test
    public void testCreateOrder(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        Assert.assertEquals(1, this.orderRepository.count());
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        Assert.assertEquals(2, this.orderRepository.count());
    }

    @Test
    @WithMockUser
    public void testSetAddressAndCourier(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.cartRepository.setAddress(addressEntity, cart.getId());
        this.cartRepository.setCourier(courierEntity, cart.getId());
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
       this.orderService.setAddressAndCourier(this.mapper.map(this.cartRepository.getCartById(cart.getId()), CartServiceModel.class));
        OrderEntity orderAfter = this.cartRepository.getCartById(this.courierRepository.findAll().get(0).getId()).getOrders().get(0);
       Assert.assertEquals(courierEntity.getName(), this.cartRepository.getCartById(cart.getId()).getCourier().getName());
       Assert.assertEquals(addressEntity.getId(), this.cartRepository.getCartById(cart.getId()).getAddress().getId());
       this.userRepository.deleteAll();
    }

    @Test
    public void testDeleteOrderById(){
        Assert.assertEquals(1, this.orderRepository.count());
        this.orderService.deleteOrderById(orderEntity.getId());
        Assert.assertEquals(0, this.orderRepository.count());
    }

    @Test
    public void testDeleteAllIsNotPaidOrders(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.orderService.createOrder("case", BigDecimal.valueOf(2), this.cartRepository.getCartById(cart.getId()));
        this.orderService.createOrder("TPU", BigDecimal.valueOf(2), this.cartRepository.getCartById(cart.getId()));
        this.orderService.deleteAllIsNotOrderedOrders(cart.getId());
        Assert.assertEquals(0, this.orderRepository.findAllOrderByIsOrderedAndCartId(false, cart.getId()).size());
    }

    @Test
    public void testFindOrderById(){
        OrderEntity orderCurrent = this.orderRepository.findAll().get(0);
        OrderEntity order = this.orderService.findOrderById(orderCurrent.getId());
       Assert.assertEquals(order.getId(),orderCurrent.getId());
    }

    @Test
    public void testSetIsPaid(){
        OrderEntity order = this.orderRepository.findAll().get(0);
        Assert.assertFalse(this.orderService.findOrderById(order.getId()).isPaid());
        this.orderService.setIsPaid(true, order.getId());
        Assert.assertTrue(this.orderService.findOrderById(order.getId()).isPaid());
    }

    @Test
    public void testUpdateOrderToPaid(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        this.orderService.updateOrderToPaid(cart.getId());
        Assert.assertTrue(this.cartRepository.getCartById(cart.getId()).getOrders().get(0).isPaid());
    }

    @Test
    public void testFindAllOrdersOrderByDateTime(){
        Assert.assertEquals(1, this.orderService.findAllOrdersOrderByDateTime().size());
    }

    @Test
    public void testFindAllByIsDeliveredOrderByDateTime(){
        this.orderService.setOrdersToDelivered(this.addressRepository.findAll().get(0).getId());
        Assert.assertEquals(0, this.orderService.findAllByIsDeliveredOrderByDateTime(false).size());
    }

    @Test
    public void testSetIsDelivered(){
        OrderEntity order = this.orderRepository.findAll().get(0);
        this.orderService.setIsDelivered(false, order.getId());
        Optional<OrderEntity> order1 = this.orderRepository.findById(order.getId());
        Assert.assertFalse(this.orderRepository.findAll().get(0).isDelivered());
    }

    @Test
    public void testFindAllOrderByIsOrderedAndCartId(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        Assert.assertEquals(1, this.orderService.findAllOrderByIsOrderedAndCartId(false, cart.getId()).size());
    }
    
    @Test
    public void TestUpdateOrderToOrdered(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        List<OrderEntity> orders = this.orderRepository.findAllOrderByIsOrderedAndCartId(false, cart.getId());
        Assert.assertFalse(orders.get(0).isOrdered());
        this.orderService.updateOrderToOrdered(cart.getId());
        Assert.assertTrue(this.cartRepository.getCartById(cart.getId()).getOrders().get(0).isOrdered());
    }

    @Test
    public void setIsOrdered(){
        OrderEntity order = this.orderRepository.findAll().get(0);
        this.orderService.setIsOrdered(false, order.getId());
        Assert.assertFalse(this.orderRepository.findOrderById(order.getId()).isOrdered());
        this.orderRepository.setIsOrdered(true, order.getId());
        Assert.assertTrue(this.orderRepository.findOrderById(order.getId()).isOrdered());
    }

    @Test
    public void testFindAllOrderByIsPaidAndCartId(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        this.orderService.createOrder("TPU", BigDecimal.valueOf(2), cart);
        List<OrderViewModel> orders = this.orderService.findAllOrderByIsPaidAndCartId(false, cart.getId());
        Assert.assertEquals(2, orders.size());
    }

    @Test
    public void testProductInUnpaidOrder(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        Assert.assertTrue(this.orderService.productInUnpaidOrder("case"));
        Assert.assertFalse(this.orderService.productInUnpaidOrder("battery"));
    }

    @Test
    public void testUnpaidProductInBrand(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        Assert.assertTrue(this.orderService.unpaidProductInBrand("Nokia"));
        Assert.assertFalse(this.orderService.unpaidProductInBrand("Apple"));
    }

    @Test
    public void testUnpaidProductInCategory(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        Assert.assertTrue(this.orderService.unpaidProductInCategory("cases"));
        Assert.assertFalse(this.orderService.unpaidProductInCategory("others"));
    }

    @Test
    public void testUnpaidProductInCourier(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.cartRepository.setCourier(courierEntity, cart.getId());
        this.cartRepository.setAddress(addressEntity, cart.getId());
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        this.orderService.setAddressAndCourier(this.mapper.map(cart, CartServiceModel.class));
        Assert.assertFalse(this.orderService.unpaidProductInCourier("speedy"));
    }

    @Test
    public void testFindAllByCartId(){
        CartEntity cart = this.cartRepository.findAll().get(0);
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cart);
        this.orderService.createOrder("TPU", BigDecimal.valueOf(2), cart);
        List<OrderViewModel> orders = this.orderService.findAllByCartId(cart.getId());
        Assert.assertEquals(2, orders.size());
    }

    @Test
    public void testFindAllOrdersByDate(){
        Assert.assertEquals(1, this.orderService.findAllOrdersByDate(LocalDate.now()).size());
    }

    @Test
    public void testSetOrdersToPaid(){
        Assert.assertFalse(this.orderRepository.findAll().get(0).isPaid());
        this.orderService.setOrdersToPaid(this.addressRepository.findAll().get(0).getId());
        Assert.assertTrue(this.orderRepository.findAll().get(0).isPaid());
    }

    @Test
    public void testSetOrdersToDelivered(){
        Assert.assertFalse(this.orderRepository.findAll().get(0).isDelivered());
        this.orderService.setOrdersToDelivered(this.addressRepository.findAll().get(0).getId());
        Assert.assertTrue(this.orderRepository.findAll().get(0).isDelivered());
    }

    @Test
    public void testFindAllByAddressId(){
        Assert.assertEquals(1, this.orderService.findAllByAddressId
                (this.addressRepository.findAll().get(0).getId()).size());
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
