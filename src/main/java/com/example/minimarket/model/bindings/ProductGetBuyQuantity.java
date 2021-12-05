package com.example.minimarket.model.bindings;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProductGetBuyQuantity {

    @NotNull(message = "Quantity cannot be null!!!")
    @DecimalMin(value = "1", message = "Тhe quantity cannot be a negative value!!!")
    private BigDecimal quantity;

    public ProductGetBuyQuantity() {
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
