package com.example.minimarket.model.bindings;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class ProductSetPriceBindingModel {

    @NotBlank(message = "Product name cannot be null or empty string!!!")
    @Size(min = 3, message = "Product name must be more than 3 characters!!!")
    private String name;

    @NotNull(message = "Quantity cannot be null!!!")
    @DecimalMin(value = "0.1", message = "Тhe quantity cannot be a negative value!!!")
    private BigDecimal newPrice;

    public ProductSetPriceBindingModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }
}
