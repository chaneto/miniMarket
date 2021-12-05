package com.example.minimarket.web;

import com.example.minimarket.model.bindings.CategoryAddBindingModel;
import com.example.minimarket.model.bindings.ProductGetBuyQuantity;
import com.example.minimarket.model.services.CategoryServiceModel;
import com.example.minimarket.services.CategoryService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ModelMapper mapper;
    private final UserService userService;
    private final OrderService orderService;

    public CategoryController(CategoryService categoryService, ModelMapper mapper, UserService userService, OrderService orderService) {
        this.categoryService = categoryService;
        this.mapper = mapper;
        this.userService = userService;
        this.orderService = orderService;
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
        redirectAttributes.addFlashAttribute("categoryAddBindingModel", categoryAddBindingModel);
        redirectAttributes.addFlashAttribute("successfulAddedCategory", true);
        return "redirect:add";
    }

    @GetMapping("/all")
    public String allCategories(Model model){
        model.addAttribute("allCategories", this.categoryService.findAll());
        return "all-categories";
    }

    @GetMapping("/delete/{name}")
    public String deleteCategory(@PathVariable String name, HttpServletRequest request, RedirectAttributes redirectAttributes){
        String referer = request.getHeader("Referer");
        if(orderService.unpaidProductInCategory(name)){
            redirectAttributes.addFlashAttribute("unpaidProductInCategory", true);
            redirectAttributes.addFlashAttribute("categoryName", name);
            return "redirect:" + referer;
        } else {
            if(this.userService.getCurrentUser().getRole().getUserRole().name().equals("ADMIN")){
            this.categoryService.deleteByName(name);
            redirectAttributes.addFlashAttribute("successfullyDeleted", true);
            redirectAttributes.addFlashAttribute("categoryName", name);
            }
            return "redirect:" + referer;
        }
    }

    @GetMapping("/allByCategory/{name}")
    public String getAllProductsByCategory(@PathVariable String name, Model model){
        model.addAttribute("allProducts", this.categoryService.getAllProducts(name));
        return "all-products";
    }

    @ModelAttribute("productGetBuyQuantity")
    public ProductGetBuyQuantity productGetBuyQuantity(){
        return new ProductGetBuyQuantity();
    }

    public boolean isAuthenticated(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().equals("anonymousUser")){
            return false;
        }else {
            return true;
        }
    }

    @ModelAttribute("orderCount")
    public int orderCount() {
        if(isAuthenticated()){
            return this.userService.getCountAllUserOrders();
        }
        return 0;
    }

    @ModelAttribute("getTotalPriceForAllOrders")
    public BigDecimal getTotalPriceForAllOrders() {
        if(isAuthenticated()){
            return this.userService.getTotalPriceForAllOrders();
        }
        return null;
    }

    @ModelAttribute("getCardId")
    public Long getCardId() {
        if(isAuthenticated()){
            return this.userService.getCurrentCartId();
        }
        return Long.valueOf(0);
    }
}
