package com.example.minimarket.model.entities;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "couriers")
public class CourierEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 3)
    private String name;

    @Min(0)
    @Max(6)
    private int rating;

    @Column(nullable = false)
    @DecimalMin("0")
    private BigDecimal shippingAmount;

    @Size(min = 3)
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    public CourierEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
