package com.example.minimarket.model.views;

import javax.persistence.Column;
import javax.validation.constraints.Size;

public class CategoryViewModel {

    private String name;
    private String description;
    private String image;

    public CategoryViewModel() {
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

    public void setImage(String image) {
        this.image = image;
    }
}
