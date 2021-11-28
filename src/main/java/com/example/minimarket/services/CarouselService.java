package com.example.minimarket.services;

import java.util.List;

public interface CarouselService {
    String firstImage();
    String secondImage();
    String thirdImage();
    List<String> getImages();
}
