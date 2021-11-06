package com.example.minimarket.model.bindings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CourierGetBindingModel {

    @NotBlank(message = "Name cannot be null or empty String!!!")
    @Size(min = 3,message = "Name must be more than 3 characters!!!")
    private String name;

    public CourierGetBindingModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
