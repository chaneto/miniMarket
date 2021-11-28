package com.example.minimarket.services;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.CartServiceModel;
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
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        productEntity3 = new ProductEntity();
        productEntity3.setName("TPU");
        productEntity3.setPrice(BigDecimal.valueOf(30));
        productEntity3.setQuantity(BigDecimal.valueOf(300));
        productEntity3.setImage("image");
        productEntity3.setDescription("The best product...");
        productEntity3.setOnPromotion(false);
        productEntity3.setCategory(categoryEntity);
        productEntity3.setBrand(brandEntity);

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
        this.addressRepository.save(addressEntity);

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
        orderEntity.setCourier(null);
        orderEntity.setAddress(null);
        orderEntity.setCart(cartEntity);
        this.orderRepository.save(orderEntity);

    }

    @Test
    public void testCreateOrder(){
        Assert.assertEquals(1, this.orderRepository.count());
        this.orderService.createOrder("case", BigDecimal.valueOf(2), cartEntity);
        Assert.assertEquals(2, this.orderRepository.count());
    }

    @Test
    public void testSetAddressAndCourier(){
        this.orderService.createOrder("case", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
       this.orderService.setAddressAndCourier(this.mapper.map(this.cartRepository.getCartById(Long.valueOf(1)), CartServiceModel.class));
        OrderEntity orderAfter = this.cartRepository.getCartById(Long.valueOf(1)).getOrders().get(0);
       Assert.assertEquals(courierEntity.getName(), this.cartRepository.getCartById(Long.valueOf(1)).getCourier().getName());
       Assert.assertEquals(addressEntity.getId(), this.cartRepository.getCartById(Long.valueOf(1)).getAddress().getId());
    }

    @Test
    public void testDeleteOrderById(){
        Assert.assertEquals(1, this.orderRepository.count());
        this.orderService.deleteOrderById(orderEntity.getId());
        Assert.assertEquals(0, this.orderRepository.count());
    }

    @Test
    public void testDeleteAllIsNotPaidOrders(){
        this.orderService.createOrder("case", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        this.orderService.createOrder("TPU", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        Assert.assertEquals(3, this.orderRepository.findAllByIsPaid(false).size());
        this.orderService.deleteAllIsNotPaidOrders(Long.valueOf(1));
        Assert.assertEquals(0, this.orderRepository.findAllByIsPaid(false).size());
    }

    @Test
    public void testFindOrderById(){
        Long id = Long.valueOf(1);
        OrderEntity order = this.orderService.findOrderById(id);
       Assert.assertEquals(id,order.getId());
    }

    @Test
    public void testSetIsPaid(){
        Assert.assertFalse(this.orderService.findOrderById(Long.valueOf(1)).isPaid());
        this.orderService.setIsPaid(true, Long.valueOf(1));
        Assert.assertTrue(this.orderService.findOrderById(Long.valueOf(1)).isPaid());
    }

    @Test
    public void testUpdateOrderToPaid(){
        Assert.assertFalse(this.orderService.findOrderById(Long.valueOf(1)).isPaid());
        this.orderService.updateOrderToPaid(Long.valueOf(1));
        Assert.assertTrue(this.orderService.findOrderById(Long.valueOf(1)).isPaid());
    }

    @Test
    public void testFindAllOrderByIsPaidAndCartId(){
        this.orderService.createOrder("case", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        this.orderService.createOrder("TPU", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        List<OrderViewModel> orders = this.orderService.findAllOrderByIsPaidAndCartId(false, Long.valueOf(1));
        Assert.assertEquals(3, orders.size());
    }

    @Test
    public void testProductInUnpaidOrder(){
        this.orderService.createOrder("case", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        Assert.assertTrue(this.orderService.productInUnpaidOrder("case"));
        Assert.assertFalse(this.orderService.productInUnpaidOrder("battery"));
    }

    @Test
    public void testUnpaidProductInBrand(){
        this.orderService.createOrder("case", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        Assert.assertTrue(this.orderService.unpaidProductInBrand("Nokia"));
        Assert.assertFalse(this.orderService.unpaidProductInBrand("Apple"));
    }

    @Test
    public void testUnpaidProductInCategory(){
        this.orderService.createOrder("case", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        Assert.assertTrue(this.orderService.unpaidProductInCategory("cases"));
        Assert.assertFalse(this.orderService.unpaidProductInCategory("others"));
    }

    @Test
    public void testUnpaidProductInCourier(){
        this.orderService.createOrder("case", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        this.orderService.setAddressAndCourier(this.mapper.map(this.cartRepository.getCartById(Long.valueOf(1)), CartServiceModel.class));
        Assert.assertTrue(this.orderService.unpaidProductInCourier("dhl"));
        Assert.assertFalse(this.orderService.unpaidProductInCourier("speedy"));
    }

    @Test
    public void testFindAllByCartId(){
        this.orderService.createOrder("case", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        this.orderService.createOrder("TPU", BigDecimal.valueOf(2), this.cartRepository.getCartById(Long.valueOf(1)));
        List<OrderViewModel> orders = this.orderService.findAllByCartId(Long.valueOf(1));
        Assert.assertEquals(3, orders.size());
    }



}
