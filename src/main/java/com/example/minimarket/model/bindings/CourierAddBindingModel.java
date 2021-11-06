package com.example.minimarket.model.bindings;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class CourierAddBindingModel {

    @Expose
    @NotBlank(message = "Name cannot be null or empty String!!!")
    @Size(min = 3,message = "Name must be more than 3 characters!!!")
    private String name;

    @Expose
    @Min(value = 0, message = "Name must be positive!!!")
    private int rating;

    @Expose
    @NotNull(message = "Amount cannot be empty String!!!")
    @DecimalMin(value = "0", message = "Name must be positive!!!")
    private BigDecimal shippingAmount;

    @Expose
    @Size(min = 3,message = "Name must be more than 3 characters!!!")
    private String imageUrl;

    public CourierAddBindingModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
