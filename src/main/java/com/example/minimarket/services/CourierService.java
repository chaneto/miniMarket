package com.example.minimarket.services;

import com.example.minimarket.model.services.CourierServiceModel;
import com.example.minimarket.model.views.CourierViewModel;

import java.io.IOException;
import java.util.List;

public interface CourierService {

    CourierServiceModel findByName(String name);

    void saveCourier(CourierServiceModel courierServiceModel);

    List<CourierViewModel> findAll();

    void deleteByName(String name);

    void seedCourierFromJson() throws IOException;

    void setCourierCart(String name);
}
