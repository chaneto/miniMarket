package com.example.minimarket.web;

import com.example.minimarket.model.bindings.BrandAddBidingModel;
import com.example.minimarket.model.bindings.ProductGetBuyQuantity;
import com.example.minimarket.model.services.BrandServiceModel;
import com.example.minimarket.services.BrandService;
import com.example.minimarket.services.ProductService;
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
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;
    private final ProductService productService;
    private final ModelMapper mapper;
    private final UserService userService;

    public BrandController(BrandService brandService, ProductService productService, ModelMapper mapper, UserService userService) {
        this.brandService = brandService;
        this.productService = productService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @GetMapping("/add")
    public String addBrand(Model model){
        if(!model.containsAttribute("brandAddBindingModel")){
            model.addAttribute("brandAddBindingModel", new BrandAddBidingModel());
            model.addAttribute("brandIsExists", false);
        }
        return "add-brand";
    }

    @PostMapping("/add")
    public String addBrandConfirm(@Valid BrandAddBidingModel brandAddBidingModel,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("brandAddBidingModel", brandAddBidingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.brandAddBidingModel", bindingResult);
            return "redirect:add";
        }
        BrandServiceModel brandServiceModel = this.mapper.map(brandAddBidingModel, BrandServiceModel.class);
        if(this.brandService.findByName(brandServiceModel.getName()) != null){
            redirectAttributes.addFlashAttribute("brandAddBidingModel", brandAddBidingModel);
            redirectAttributes.addFlashAttribute("brandIsExists", true);
            return "redirect:add";
        }
        this.brandService.saveBrand(brandServiceModel);
        return "redirect:/";
    }

    @GetMapping("/all")
    public String allBrands(Model model){
        model.addAttribute("allBrands", this.brandService.getAllBrands());
        return "all-brands";
    }

    @GetMapping("/delete/{name}")
    public String deleteBrand(@PathVariable String name){
        this.brandService.deleteByName(name);
        return "redirect:/";
    }

    @GetMapping("/allByBrand/{name}")
    public String getAllProductsByCategory(@PathVariable String name, Model model){
        model.addAttribute("getAllProductsByBrand", this.productService.findAllByBrand(name));
        return "all-products-by-brands";
    }

    @ModelAttribute("productGetBuyQuantity")
    public ProductGetBuyQuantity productGetBuyQuantity(){
        return new ProductGetBuyQuantity();
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
