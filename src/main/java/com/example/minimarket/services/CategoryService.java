package com.example.minimarket.services;

import com.example.minimarket.model.entities.CategoryEntity;
import com.example.minimarket.model.services.CategoryServiceModel;
import com.example.minimarket.model.views.CategoryViewModel;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    CategoryServiceModel findByName(String name);

    CategoryEntity findByNameEntity(String name);

    List<CategoryViewModel> findAll();

    List<String> getAllCategoryName();

    void deleteByName(String name);

    void seedCategories() throws IOException;

    void saveCategory(CategoryServiceModel categoryServiceModel);
}