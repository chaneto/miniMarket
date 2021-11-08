package com.example.minimarket.model.entities;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.br.CNPJ;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.valueOf;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 3)
    private String name;

    @Column(nullable = false)
    @DecimalMin("0")
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    @Size(min = 10)
    private String description;

    @Column(nullable = false)
    @DecimalMin("0")
    private BigDecimal quantity;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(min = 3)
    private String image;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    private BrandEntity brand;

    @ManyToOne//(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CategoryEntity category;

    public ProductEntity() {
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

    public BrandEntity getBrand() {
        return brand;
    }

    public void setBrand(BrandEntity brand) {
        this.brand = brand;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

}
