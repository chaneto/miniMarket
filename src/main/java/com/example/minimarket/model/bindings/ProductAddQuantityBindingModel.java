package com.example.minimarket.model.bindings;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class ProductAddQuantityBindingModel {

    @NotBlank(message = "Product name cannot be null or empty string!!!")
    @Size(min = 3, message = "Product name must be more than 3 characters!!!")
    private String name;

    @NotNull(message = "Quantity cannot be null!!!")
    @DecimalMin(value = "0", message = "Тhe quantity cannot be a negative value!!!")
    private BigDecimal quantity;

    public ProductAddQuantityBindingModel() {
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
