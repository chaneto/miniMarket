package com.example.minimarket.services;

import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.entities.CategoryEntity;
import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.model.services.BrandServiceModel;
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
public class BrandServiceImplTest {

    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    BrandService brandService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper mapper;

    BrandEntity brandEntity1;
    BrandEntity brandEntity3;
    ProductEntity productEntity1;
    ProductEntity productEntity3;
    CategoryEntity categoryEntity;

    @Before
    public void setup(){
        this.brandRepository.deleteAll();

        brandEntity1 = new BrandEntity();
        brandEntity1.setName("Nokia");
        brandEntity1.setImage("imageNokia");

        brandEntity3 = new BrandEntity();
        brandEntity3.setName("Apple");
        brandEntity3.setImage("imageApple");

        this.brandRepository.save(brandEntity1);
        this.brandRepository.save(brandEntity3);

    }

    @Test
    public void testFindByName(){
        this.brandRepository.save(brandEntity1);
        String brandName = brandEntity1.getName();
        BrandServiceModel brand = this.brandService.findByName(brandName);
        Assert.assertEquals(brandName, brand.getName());
    }

    @Test
    public void testFindByNameWithNonExistingBrand(){
        this.brandRepository.save(brandEntity1);
        String brandName = "Mototrola";
        BrandServiceModel brand = this.brandService.findByName(brandName);
        Assert.assertNull(brand);
    }

    @Test
    public void testFindByNameEntity(){
        this.brandRepository.save(brandEntity1);
        String brandName = brandEntity1.getName();
        BrandEntity brand = this.brandService.findByNameEntity(brandName);
        Assert.assertEquals(brandName, brand.getName());
    }

    @Test
    public void testGetAllBrands(){
        Assert.assertEquals(2, this.brandService.getAllBrands().size());
    }

    @Test
    public void testGetAllProducts(){
        saveProduct();
        List<ProductViewModel> products = this.brandService.getAllProducts(brandEntity1.getName());
        Assert.assertEquals(2, products.size());
    }

    @Test
    public void testSaveBrand(){
        this.brandRepository.deleteAll();
        Assert.assertEquals(0, this.brandRepository.count());
        this.brandService.saveBrand(this.mapper.map(brandEntity1, BrandServiceModel.class));
        this.brandService.saveBrand(this.mapper.map(brandEntity3, BrandServiceModel.class));
        Assert.assertEquals(2, this.brandRepository.count());
    }

    @Test
    public void testDeleteBrandByName(){
        Assert.assertEquals(2, this.brandRepository.count());
        this.brandService.deleteByName(brandEntity1.getName());
        Assert.assertEquals(1, this.brandRepository.count());
    }



    public void saveProduct(){
        categoryEntity= new CategoryEntity();
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
        productEntity1.setBrand(brandEntity1);
        productEntity1.setDiscountRate(BigDecimal.valueOf(3));
        productEntity1.setPromotionPrice(BigDecimal.valueOf(10));

        productEntity3 = new ProductEntity();
        productEntity3.setName("TPU");
        productEntity3.setPrice(BigDecimal.valueOf(30));
        productEntity3.setQuantity(BigDecimal.valueOf(300));
        productEntity3.setImage("image");
        productEntity3.setDescription("The best product...");
        productEntity3.setOnPromotion(false);
        productEntity3.setCategory(categoryEntity);
        productEntity3.setBrand(brandEntity1);
        productEntity3.setDiscountRate(BigDecimal.valueOf(5));
        productEntity3.setPromotionPrice(BigDecimal.valueOf(30));

        this.productRepository.save(productEntity1);
        this.productRepository.save(productEntity3);
    }

}
