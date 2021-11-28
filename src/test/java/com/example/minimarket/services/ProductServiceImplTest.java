package com.example.minimarket.services;


import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.entities.CategoryEntity;
import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.model.services.ProductServiceModel;
import com.example.minimarket.model.views.ProductViewModel;
import com.example.minimarket.repositories.BrandRepository;
import com.example.minimarket.repositories.CategoryRepository;
import com.example.minimarket.repositories.ProductRepository;
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
public class ProductServiceImplTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelMapper mapper;

    ProductEntity productEntity1;
    ProductEntity productEntity3;
    CategoryEntity categoryEntity;
    BrandEntity brandEntity;

    @Before
    public void setup(){
        this.brandRepository.deleteAll();
        this.categoryRepository.deleteAll();
        this.productRepository.deleteAll();

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
    }

    @Test
    public void testFindByNameWithExistingName(){
        String productName = "TPU";
        ProductViewModel product = this.productService.findByName(productName);
        Assert.assertEquals(productName, product.getName());
    }

    @Test(expected = NullPointerException.class)
    public void testFindByNameWithNonExistingName(){
        String productName = "screen";
        ProductViewModel product = this.productService.findByName(productName);
        Assert.assertEquals(productName, product.getName());
    }

    @Test
    public void testFindByNameEntity(){
        String productName = "TPU";
        ProductEntity product = this.productService.findByNameEntity(productName);
        Assert.assertEquals(productName, product.getName());
    }

    @Test
    public void testSeedProduct(){
        this.productRepository.deleteAll();
        this.productService.seedProduct(this.mapper.map(productEntity1, ProductServiceModel.class));
        this.productService.seedProduct(this.mapper.map(productEntity3, ProductServiceModel.class));
        Assert.assertEquals(2, this.productRepository.count());
    }

    @Test
    public void testSeedProductWithoutQuantity(){
        this.productRepository.deleteAll();
        productEntity1.setQuantity(BigDecimal.valueOf(0));
        this.productService.seedProduct(this.mapper.map(productEntity1, ProductServiceModel.class));
        Assert.assertFalse(productEntity1.isAvailable());
    }

    @Test
    public void testFindAllOrderByName(){
        List<ProductViewModel> products = this.productService.findAllOrderByName();
        Assert.assertEquals(2, this.productRepository.count());
        Assert.assertEquals(products.get(0).getName(), "TPU");
    }

    @Test
    public void testGetPromotionProduct(){
        List<ProductViewModel> products = this.productService.getPromotionProduct();
        Assert.assertEquals(1, products.size());
        Assert.assertEquals(products.get(0).getName(), "case");
    }

    @Test
    public void testFindTop4ByQuantityDesc(){
        List<ProductViewModel> products = this.productService.findTop4ByQuantityDesc();
        Assert.assertEquals(2, this.productRepository.count());
    }

    @Test
    public void testFindAllOrderByQuantity(){
        List<ProductViewModel> products = this.productService.findAllOrderByQuantity();
        Assert.assertEquals(2, this.productRepository.count());
        Assert.assertEquals(products.get(0).getName(), "case");
    }

    @Test
    public void testDeleteProductByName(){
        String productName = "TPU";
        Assert.assertEquals(2, this.productRepository.count());
        this.productService.deleteProductByName(productName);
        Assert.assertEquals(1, this.productRepository.count());
    }

    @Test
    public void testAddQuantity(){
        BigDecimal addQuantity = BigDecimal.valueOf(33);
        BigDecimal result = productEntity3.getQuantity().add(addQuantity);
        this.productService.addQuantity(addQuantity, productEntity3.getName());
        Assert.assertEquals(result.stripTrailingZeros(), this.productService.findByName(productEntity3.getName()).getQuantity().stripTrailingZeros());
    }

    @Test
    public void testGetAllProductsName(){
        List<String> names = this.productService.getAllProductsName();
        Assert.assertEquals(2, names.size());
    }

    @Test
    public void testByuProduct(){
        BigDecimal subtractQuantity = BigDecimal.valueOf(77);
        BigDecimal result = productEntity3.getQuantity().subtract(subtractQuantity);
        this.productService.buyProduct(productEntity3.getName(), subtractQuantity);
        Assert.assertEquals(result.stripTrailingZeros(), this.productService.findByName(productEntity3.getName()).getQuantity().stripTrailingZeros());
    }

    @Test
    public void testByuProductWithNotEnoughQuantity(){
        BigDecimal subtractQuantity = BigDecimal.valueOf(555);
        BigDecimal result = productEntity3.getQuantity().subtract(subtractQuantity);
        this.productService.buyProduct(productEntity3.getName(), subtractQuantity);
    }

    @Test
    public void testQuantityIsEnough(){
        Assert.assertTrue(this.productService.quantityIsEnough("TPU", BigDecimal.valueOf(100)));
        Assert.assertFalse(this.productService.quantityIsEnough("TPU", BigDecimal.valueOf(500)));
    }

    @Test
    public void testSubtractQuantity(){
        BigDecimal subtractQuantity = BigDecimal.valueOf(77);
        BigDecimal result = productEntity3.getQuantity().subtract(subtractQuantity);
        this.productService.subtractQuantity(subtractQuantity, productEntity3.getName());
        Assert.assertEquals(result.stripTrailingZeros(), this.productService.findByName(productEntity3.getName()).getQuantity().stripTrailingZeros());
    }

    @Test
    public void testGetProductForPromotion(){
        List<ProductViewModel> products = this.productService.findTop4ByQuantity();
        Assert.assertEquals(2, products.size());
    }

    @Test
    public void testUpdatePricePromotionProduct(){
        BigDecimal oldPrice = productEntity1.getPrice();
        BigDecimal newPrice = oldPrice.divide(BigDecimal.valueOf(2));
        this.productService.updatePriceTop4ByQuantityProduct();
        Assert.assertEquals(newPrice.stripTrailingZeros(), this.productService.findByName("case").getPrice().stripTrailingZeros());
    }

    @Test
    public void testUpdatePromotionProduct(){
        BigDecimal oldPrice = productEntity1.getPrice();
        BigDecimal newPrice = oldPrice.multiply(BigDecimal.valueOf(2));
        this.productService.updateTop4ByQuantityProduct();
        Assert.assertEquals(newPrice.stripTrailingZeros(), this.productService.findByName("case").getPrice().stripTrailingZeros());
    }

    @Test
    public void testGetById(){
        Long id = productEntity3.getId();
        ProductViewModel product = this.productService.getById(id);
        Assert.assertEquals(product.getName(), productEntity3.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByNonExistingId(){
        ProductViewModel product = this.productService.getById(Long.valueOf(-50));
    }

    @Test
    public void testSetPrice(){
        String name = productEntity3.getName();
        BigDecimal newPrice = BigDecimal.valueOf(333);
        this.productService.setPrice(newPrice, name);
        Assert.assertEquals(newPrice.stripTrailingZeros(), this.productService.findByName(name).getPrice().stripTrailingZeros());
    }

    @Test
    public void testFindAllByIsOnPromotionIsTrue(){
        List<ProductEntity> products = this.productService.findAllByIsOnPromotionIsTrue();
        Assert.assertEquals(1, products.size());
    }

    @Test

    public void testRefreshPromotionProduct(){

        List<ProductEntity> products = this.productService.findAllByIsOnPromotionIsTrue();
        Assert.assertEquals(1, products.size());
        this.productService.refreshPromotionProduct();
         products = this.productService.findAllByIsOnPromotionIsTrue();
        Assert.assertEquals(2, products.size());
    }
}
