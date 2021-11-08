package com.example.minimarket.model.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 3)
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(min = 10)
    private String description;

    @Column(columnDefinition = "TEXT")
    @Size(min = 3)
    private String image;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER,cascade = CascadeType.REFRESH, orphanRemoval = true)
    private List<ProductEntity> products = new ArrayList<>();

    public CategoryEntity() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String imageUrl) {
        this.image = imageUrl;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }
}
