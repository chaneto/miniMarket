package com.example.minimarket.model.bindings;

import com.google.gson.annotations.Expose;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CategoryAddBindingModel {


    @Expose
    @NotBlank(message = "Category name cannot be null or empty String!!!")
    @Size(min = 3, message = "Category name must be more than 3 characters!!!")
    private String name;

    @Expose
    @NotBlank(message = "Description cannot be null or empty String!!!")
    @Size(min = 10, message = "Description name must be more than 3 characters!!!")
    private String description;

    @Expose
    @Size(min = 3, message = "Image length must be more than 3 characters!!!")
    private String image;

    public CategoryAddBindingModel() {
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
