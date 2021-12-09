package com.example.minimarket.web;

import com.example.minimarket.model.entities.*;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.repositories.*;
import com.example.minimarket.services.UserService;
import org.junit.Assert;
import org.junit.Before;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    private WebApplicationContext context;

    ProductEntity productEntity1;
    OrderEntity orderEntity;
    CartEntity cartEntity;
    CategoryEntity categoryEntity;
    BrandEntity brandEntity;

    @Before
    public void setup(){

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        this.categoryRepository.deleteAll();
        this.brandRepository.deleteAll();
        this.productRepository.deleteAll();
        this.cartRepository.deleteAll();
        this.orderRepository.deleteAll();

        categoryEntity = new CategoryEntity();
        categoryEntity.setName("Protectors");
        categoryEntity.setDescription("Protectors for all smartphones");
        categoryEntity.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        this.categoryRepository.save(categoryEntity);

        brandEntity = new BrandEntity();
        brandEntity.setName("Nokia");
        brandEntity.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        this.brandRepository.save(brandEntity);

        productEntity1 = new ProductEntity();
        productEntity1.setName("case");
        productEntity1.setPrice(BigDecimal.valueOf(2.50));
        productEntity1.setDescription("The best product...");
        productEntity1.setQuantity(BigDecimal.valueOf(20));
        productEntity1.setImage("https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg");
        productEntity1.setOnPromotion(true);
        productEntity1.setCategory(categoryEntity);
        productEntity1.setBrand(brandEntity);
        productEntity1.setPromotionPrice(BigDecimal.valueOf(33));
        productEntity1.setDiscountRate(BigDecimal.valueOf(22));
        this.productRepository.save(productEntity1);

        cartEntity = new CartEntity();
        cartEntity.setTotalPrice(BigDecimal.valueOf(99));
        this.cartRepository.save(cartEntity);


        orderEntity = new OrderEntity();
        orderEntity.setProduct(productEntity1);
        orderEntity.setCart(cartEntity);
        orderEntity.setTotalPrice(BigDecimal.valueOf(99));
        orderEntity.setDateTime(LocalDateTime.now());
        orderEntity.setOrdered(true);
        orderEntity.setUsername("admin");
        List<OrderEntity> orders = List.of(orderEntity);
        cartEntity.setOrders(orders);
        this.orderRepository.save(orderEntity);

    }

    @Test
    public void testAddProduct() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-product"))
        .andExpect(model().attributeExists("allCategory"))
        .andExpect(model().attributeExists("allBrands"));
    }

    @Test
    public void testAddProductConfirm() throws Exception {
        authenticate();
        Assert.assertEquals(1, this.productRepository.count());
        this.mockMvc
                .perform(post("/products/add")
                        .param("name", "screen")
                        .param("price", "2.50")
                        .param("description", "the best product...")
                        .param("quantity", "99")
                        .param("image", "https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg")
                        .param("onPromotion", "true")
                        .param("brand", "nokia")
                        .param("category", "protectors")
                       .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
        Assert.assertEquals(2, this.productRepository.count());

    }

    @Test
    public void testAddProductConfirmWithExistingProduct() throws Exception {
        authenticate();
        this.productRepository.save(productEntity1);
        Assert.assertEquals(1, this.productRepository.count());
        this.mockMvc
                .perform(post("/products/add")
                        .param("name", "case")
                        .param("price", "2.50")
                        .param("description", "the best product...")
                        .param("quantity", "99")
                        .param("image", "https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg")
                        .param("onPromotion", "true")
                        .param("brand", "nokia")
                        .param("category", "protectors")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
        Assert.assertEquals(1, this.productRepository.count());

    }

    @Test
    public void testAddProductConfirmWithInvalidData() throws Exception {
        authenticate();
        this.productRepository.save(productEntity1);
        Assert.assertEquals(1, this.productRepository.count());
        this.mockMvc
                .perform(post("/products/add")
                        .param("name", "")
                        .param("price", "-2.50")
                        .param("description", "the best product...")
                        .param("quantity", "99")
                        .param("image", "https://s13emagst.akamaized.net/products/33285/33284232/images/res_14a6987ed029429aaaebc06b2b9e2ca1.jpg")
                        .param("onPromotion", "true")
                        .param("brand", "nokia")
                        .param("category", "protectors")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:add"));
        Assert.assertEquals(1, this.productRepository.count());

    }

    @Test
    public void testAllProductsView() throws Exception {
        this.mockMvc
                .perform(get("/products/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-products"));

    }

    @Test
    public void testDeleteProduct() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/delete/case")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testDeleteProductWithNonExistingProduct() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/delete/battery")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void testSetPriceConfirm() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/products/setProductPrice")
                        .param("name", "case")
                .param("newPrice", "999")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void testSetPriceConfirmWithInvalidPrice() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/products/setProductPrice")
                        .param("name", "case")
                        .param("newPrice", "null")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }


    @Test
    @WithMockUser
    public void testSetDiscountRateConfirm() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/products/setDiscountRate")
                        .param("name", "case")
                        .param("newPrice", "999")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testSeeProductsQuantities() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/productsMenu"))
                .andExpect(view().name("products-menu"))
                .andExpect(model().attributeExists("allProducts"));
    }

    @Test
    public void testAllProductsOrderByQuantities() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/allProductsOrderByQuantity"))
                .andExpect(view().name("products-menu"))
                .andExpect(model().attributeExists("allProducts"));
    }

    @Test
    public void testAllProductsOrderByQuantitiesDesk() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/allProductsOrderByQuantityDesc"))
                .andExpect(view().name("products-menu"))
                .andExpect(model().attributeExists("allProducts"));
    }

    @Test
    public void testAllProductsOrderByPrice() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/allProductsOrderByPrice"))
                .andExpect(view().name("products-menu"))
                .andExpect(model().attributeExists("allProducts"));
    }

    @Test
    public void testAllProductsOrderByPriceDesc() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/allProductsOrderByPriceDesc"))
                .andExpect(view().name("products-menu"))
                .andExpect(model().attributeExists("allProducts"));
    }

    @Test
    public void testSeeAllProductsOrderByID() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/allProductsOrderByID"))
                .andExpect(view().name("products-menu"))
                .andExpect(model().attributeExists("allProducts"));
    }

    @Test
    public void testAllProductsOrderByPrice1() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/allProductsOrderByPrice1...9"))
                .andExpect(view().name("all-products"))
                .andExpect(model().attributeExists("allProducts"));
    }

    @Test
    public void testAllProductsOrderByPriceDesc1() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/allProductsOrderByPrice9...1"))
                .andExpect(view().name("all-products"))
                .andExpect(model().attributeExists("allProducts"));
    }


    @Test
    @WithMockUser
    public void testSetDiscountRateConfirmWithInvalidData() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/products/setDiscountRate")
                        .param("name", "")
                        .param("newPrice", "null")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testAddQuantity() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/addQuantity/case"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-quantity"))
        .andExpect(model().attributeExists("allProductsName"))
        .andExpect(model().attributeExists("productName"));
    }

    @Test
    public void testAddQuantityConfirm() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/products/addQuantity")
                .param("name", "case")
                .param("quantity", "999")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/productsMenu"));
    }

    @Test
    public void testAddQuantityConfirmWithInvalidData() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/products/addQuantity")
                        .param("name", "c")
                        .param("quantity", "null")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testAddProductToCart() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/products/addProduct/case")
                        .param("quantity", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testAddProductToCartWithEnougthQuantity() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/products/addProduct/case")
                        .param("quantity", "99999")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testAddProductToCartWithIncorrectQuantity() throws Exception {
        authenticate();
        this.mockMvc
                .perform(post("/products/addProduct/case")
                        .param("quantity", "null")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testDetails() throws Exception {
        this.mockMvc
                .perform(get("/products/details/case"))
                .andExpect(status().isOk())
                .andExpect(view().name("details"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    public void testPromotionProductsView() throws Exception {
        authenticate();
        this.mockMvc
                .perform(get("/products/promotion"))
                .andExpect(status().isOk())
                .andExpect(view().name("promotional-products"))
                .andExpect(model().attributeExists("promotionsProducts"));
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

    private ModelAndView getModelAndView(MvcResult mvcResult) {
        ModelAndView mav = mvcResult.getModelAndView();
        if (mav == null) {
            fail("No ModelAndView found");
        }
        return mav;
    }

}
