package com.example.minimarket.services;

import com.example.minimarket.model.entities.BrandEntity;
import com.example.minimarket.model.entities.CategoryEntity;
import com.example.minimarket.model.entities.ProductEntity;
import com.example.minimarket.model.services.CategoryServiceModel;
import com.example.minimarket.model.views.CategoryViewModel;
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
public class CategoryServiceImplTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelMapper mapper;

    CategoryEntity categoryEntity1;
    CategoryEntity categoryEntity3;
    ProductEntity productEntity1;
    ProductEntity productEntity3;
    BrandEntity brandEntity;

    @Before
    public void setup(){
        this.categoryRepository.deleteAll();

        categoryEntity1= new CategoryEntity();
        categoryEntity1.setName("cases");
        categoryEntity1.setDescription("Cases for all smartphones...");
        categoryEntity1.setImage("image");

        categoryEntity3= new CategoryEntity();
        categoryEntity3.setName("batteries");
        categoryEntity3.setDescription("Cases for all smartphones...");
        categoryEntity3.setImage("image");
    }

    @Test
    public void testFindByName(){
        this.categoryRepository.save(categoryEntity1);
        String courierName = categoryEntity1.getName();
        CategoryServiceModel category = this.categoryService.findByName(courierName);
        Assert.assertEquals(courierName, category.getName());
    }

    @Test
    public void testFindByNonExistingName(){
        this.categoryRepository.save(categoryEntity1);
        CategoryServiceModel category = this.categoryService.findByName("kabels");
        Assert.assertNull(category);
    }

    @Test
    public void testFindByNameEntity(){
        this.categoryRepository.save(categoryEntity1);
        String courierName = categoryEntity1.getName();
        CategoryEntity category = this.categoryService.findByNameEntity(courierName);
        Assert.assertEquals(courierName, category.getName());
    }

    @Test
    public void testSaveCategory(){
        Assert.assertEquals(0, this.categoryRepository.count());
        this.categoryService.saveCategory(this.mapper.map(categoryEntity1, CategoryServiceModel.class));
        this.categoryService.saveCategory(this.mapper.map(categoryEntity3, CategoryServiceModel.class));
        Assert.assertEquals(2, this.categoryRepository.count());
    }

    @Test
    public void testFindAllCategories(){
        this.categoryRepository.save(categoryEntity1);
        this.categoryRepository.save(categoryEntity3);
        List<CategoryViewModel> categories = this.categoryService.findAll();
        Assert.assertEquals(2, categories.size());
    }

    @Test
    public void testFindAllCategoriesName(){
        this.categoryRepository.save(categoryEntity1);
        this.categoryRepository.save(categoryEntity3);
        List<String> names = this.categoryService.getAllCategoryName();
        Assert.assertEquals(2, names.size());
    }

    @Test
    public void testGetAllProducts(){
        this.categoryRepository.save(categoryEntity1);
        saveProduct();
        List<ProductViewModel> products = this.categoryService.getAllProducts(categoryEntity1.getName());
        Assert.assertEquals(2, products.size());
    }

    @Test
    public void testDeleteCategoryByName(){
        this.categoryRepository.save(categoryEntity1);
        this.categoryRepository.save(categoryEntity3);
        Assert.assertEquals(2, this.categoryRepository.count());
        this.categoryService.deleteByName(categoryEntity1.getName());
        Assert.assertEquals(1, this.categoryRepository.count());
    }

    public void saveProduct(){
        brandEntity = new BrandEntity();
        brandEntity.setName("Nokia");
        brandEntity.setImage("image");
        this.brandRepository.save(brandEntity);

        productEntity1 = new ProductEntity();
        productEntity1.setName("case");
        productEntity1.setPrice(BigDecimal.valueOf(10));
        productEntity1.setQuantity(BigDecimal.valueOf(100));
        productEntity1.setImage("image");
        productEntity1.setDescription("The best product...");
        productEntity1.setOnPromotion(true);
        productEntity1.setCategory(categoryEntity1);
        productEntity1.setBrand(brandEntity);

        productEntity3 = new ProductEntity();
        productEntity3.setName("TPU");
        productEntity3.setPrice(BigDecimal.valueOf(30));
        productEntity3.setQuantity(BigDecimal.valueOf(300));
        productEntity3.setImage("image");
        productEntity3.setDescription("The best product...");
        productEntity3.setOnPromotion(false);
        productEntity3.setCategory(categoryEntity1);
        productEntity3.setBrand(brandEntity);

        this.productRepository.save(productEntity1);
        this.productRepository.save(productEntity3);
    }
}
