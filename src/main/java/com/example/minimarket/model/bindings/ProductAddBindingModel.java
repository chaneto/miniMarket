package com.example.minimarket.model.bindings;

import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductAddBindingModel {

    @NotBlank(message = "Product name cannot be null or empty string!!!")
    @Size(min = 3, message = "Product name must be more than 3 characters!!!")
    private String name;

    @NotNull(message = "Price cannot be null!!!")
    @DecimalMin(value = "0", message = "Тhe price must be a positive value!!!")
    private BigDecimal price;

    @Size(min = 10, message = "Description must be more than 10 characters!!!")
    private String description;

    @NotNull(message = "Quantity cannot be null!!!")
    @DecimalMin(value = "0", message = "Тhe quantity cannot be a negative value!!!")
    private BigDecimal quantity;

    @NotBlank(message = "Image cannot be null or empty string!!!")
    @Size(min = 3, message = "Image must be more than 3 characters!!!")
    private String image;

    @NotBlank(message = "Brand cannot be null or empty string!!!")
    private String brand;

    @NotBlank(message = "Category cannot be null or empty string!!!")
    private String category;

    public ProductAddBindingModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
