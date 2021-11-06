package com.example.minimarket.web;

import com.example.minimarket.model.bindings.CategoryAddBindingModel;
import com.example.minimarket.model.services.CategoryServiceModel;
import com.example.minimarket.services.CategoryService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ModelMapper mapper;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, ModelMapper mapper, UserService userService) {
        this.categoryService = categoryService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @GetMapping("/add")
    public String addCategory(Model model){
        if(!model.containsAttribute("categoryAddBindingModel")){
            model.addAttribute("categoryAddBindingModel", new CategoryAddBindingModel());
            model.addAttribute("categoryIsExists", false);
        }
        return "add-category";
    }

    @PostMapping("/add")
    public String addCategoryConfirm(@Valid CategoryAddBindingModel categoryAddBindingModel,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("categoryAddBindingModel", categoryAddBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.categoryAddBindingModel", bindingResult);
            return "redirect:add";
        }
        CategoryServiceModel categoryServiceModel = this.mapper.map(categoryAddBindingModel, CategoryServiceModel.class);
        if(this.categoryService.findByName(categoryServiceModel.getName()) != null){
            redirectAttributes.addFlashAttribute("categoryAddBindingModel", categoryAddBindingModel);
            redirectAttributes.addFlashAttribute("categoryIsExists", true);
            return "redirect:add";
        }
        this.categoryService.saveCategory(categoryServiceModel);
        return "redirect:/";
    }

    @GetMapping("/all")
    public String allCategories(Model model){
        model.addAttribute("allCategories", this.categoryService.findAll());
        return "all-categories";
    }

    @GetMapping("/delete/{name}")
    public String deleteProduct(@PathVariable String name){
        this.categoryService.deleteByName(name);
        return "redirect:/";
    }


    @ModelAttribute("orderCount")
    public int orderCount(){
        return this.userService.getCountAllUserOrders();
    }

    @ModelAttribute("getTotalPriceForAllOrders")
    public BigDecimal getTotalPriceForAllOrders(){
        return this.userService.getTotalPriceForAllOrders();
    }

    @ModelAttribute("getCardId")
    public Long getCardId(){
        return this.userService.getCartId();
    }
}
