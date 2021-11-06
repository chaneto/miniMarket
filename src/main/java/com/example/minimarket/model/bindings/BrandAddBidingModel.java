package com.example.minimarket.model.bindings;

import com.google.gson.annotations.Expose;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class BrandAddBidingModel {

    @Expose
    @NotBlank(message = "Brand cannot be empty string or null!!!")
    @Size(min = 1, message = "Brand length must be more than 1 character!!!")
    private String name;

    @Expose
    @Size(min = 3, message = "Brand length must be more than 3 character!!!")
    private String image;

    public BrandAddBidingModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
