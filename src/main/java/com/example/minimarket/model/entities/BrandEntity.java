package com.example.minimarket.model.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
public class BrandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 1)
    private String name;

    @Size(min = 3)
    @Column(columnDefinition = "TEXT")
    private String image;

    @OneToMany(mappedBy = "brand", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, orphanRemoval = true)
    private List<ProductEntity> products = new ArrayList<>();

    public BrandEntity() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }
}
