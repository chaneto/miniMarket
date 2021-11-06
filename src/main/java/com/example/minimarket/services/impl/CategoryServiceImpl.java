package com.example.minimarket.services.impl;

import com.example.minimarket.model.bindings.CategoryAddBindingModel;
import com.example.minimarket.model.entities.CategoryEntity;
import com.example.minimarket.model.services.CategoryServiceModel;
import com.example.minimarket.model.views.CategoryViewModel;
import com.example.minimarket.repositories.CategoryRepository;
import com.example.minimarket.services.CategoryService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final Resource categoryFile;
    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;
    private final Gson gson;

    public CategoryServiceImpl(@Value("classpath:init/categories.json") Resource categoryFile, CategoryRepository categoryRepository, ModelMapper mapper, Gson gson) {
        this.categoryFile = categoryFile;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.gson = gson;
    }

    @Override
    public CategoryServiceModel findByName(String name) {
        CategoryEntity categoryEntity = this.categoryRepository.findByName(name);
        CategoryServiceModel categoryServiceModel = null;
        if(categoryEntity != null){
            categoryServiceModel = this.mapper.map(categoryEntity, CategoryServiceModel.class);
        }
        return categoryServiceModel;
    }

    @Override
    public CategoryEntity findByNameEntity(String name) {
        return this.categoryRepository.findByName(name);
    }

    @Override
    public void saveCategory(CategoryServiceModel categoryServiceModel) {
        CategoryEntity categoryEntity = this.mapper.map(categoryServiceModel, CategoryEntity.class);
        this.categoryRepository.save(categoryEntity);
    }

    @Override
    public List<CategoryViewModel> findAll() {
        List <CategoryEntity> categories = this.categoryRepository.findAll();
        List <CategoryViewModel> categoriesView = new ArrayList<>();
        for(CategoryEntity category : categories){
           CategoryViewModel categoryViewModel = this.mapper.map(category, CategoryViewModel.class);
           categoriesView.add(categoryViewModel);
        }
        return categoriesView;
    }

    @Override
    public List<String> getAllCategoryName() {
        return this.categoryRepository.getAllCategoryName();
    }

        @Override
        public void seedCategories() throws IOException {
            if(this.categoryRepository.count() == 0){
                CategoryAddBindingModel[] categories = this.gson.fromJson(Files.readString(Path.of(categoryFile.getURI())), CategoryAddBindingModel[].class);
                for(CategoryAddBindingModel category: categories){
                    CategoryEntity categoryEntity = this.mapper.map(category, CategoryEntity.class);
                    if(this.categoryRepository.findByName(categoryEntity.getName()) == null){
                    this.categoryRepository.save(categoryEntity);
                    }
                }
            }
        }

    @Override
    public void deleteByName(String name) {
        this.categoryRepository.deleteByName(name);
    }
}
