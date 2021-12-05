package com.example.minimarket.services;


import com.example.minimarket.services.impl.CarouselServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CarouselServiceImplTest {

    @Autowired
    CarouselServiceImpl carouselService;

    private Logger LOGGER = LoggerFactory.getLogger(CarouselServiceImpl.class);

    List<String> images = new ArrayList<>();

    @Before
    public void setup(){
        images = List.of("img1.jpg", "img2.jpg", "img3.jpg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAfterInitialize(){
        List<String> images = List.of("image1", "image2");
        carouselService = new CarouselServiceImpl(images);
        this.carouselService.afterInitialize();
    }

    @Test
    public void testFirstImage(){
        Assert.assertEquals(images.get(0), this.carouselService.firstImage());
    }

    @Test
    public void testSecondImage(){
        Assert.assertEquals(images.get(1), this.carouselService.secondImage());
    }

    @Test
    public void testThirdImage(){
        Assert.assertEquals(images.get(2), this.carouselService.thirdImage());
    }

    @Test
    public void testGetImages(){
        Assert.assertEquals(5, this.carouselService.getImages().size());
    }

    @Test
    public void testSetImages(){
        List<String> newImages = List.of("image1", "image2", "image3", "image4", "image5", "image6");
        List<String> oldImages = this.carouselService.getImages();
        Assert.assertEquals(5, this.carouselService.getImages().size());
        this.carouselService.setImages(newImages);
        Assert.assertEquals(6, this.carouselService.getImages().size());
        this.carouselService.setImages(oldImages);
    }

    @Test
    public void testSetLogger(){
        Logger newLogger = null;
        Assert.assertEquals(LOGGER, this.carouselService.getLOGGER());
        this.carouselService.setLOGGER(newLogger);
        Assert.assertNull(this.carouselService.getLOGGER());
    }


}
