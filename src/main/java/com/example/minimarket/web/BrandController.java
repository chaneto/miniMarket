package com.example.minimarket.web;

import com.example.minimarket.model.bindings.BrandAddBidingModel;
import com.example.minimarket.model.bindings.ProductGetBuyQuantity;
import com.example.minimarket.model.services.BrandServiceModel;
import com.example.minimarket.services.BrandService;
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
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;
    private final ModelMapper mapper;
    private final UserService userService;
    private final OrderService orderService;

    public BrandController(BrandService brandService, ModelMapper mapper, UserService userService, OrderService orderService) {
        this.brandService = brandService;
        this.mapper = mapper;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/add")
    public String addBrand(Model model){
        if(!model.containsAttribute("brandAddBidingModel")){
            model.addAttribute("brandAddBidingModel", new BrandAddBidingModel());
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
        redirectAttributes.addFlashAttribute("brandAddBidingModel", brandAddBidingModel);
        redirectAttributes.addFlashAttribute("successfulAddedBrand", true);
        return "redirect:add";
    }

    @GetMapping("/all")
    public String allBrands(Model model){
        model.addAttribute("allBrands", this.brandService.getAllBrands());
        return "all-brands";
    }

    @GetMapping("/delete/{name}")
    public String deleteBrand(@PathVariable String name, HttpServletRequest request, RedirectAttributes redirectAttributes){
        String referer = request.getHeader("Referer");
        if(orderService.unpaidProductInBrand(name)){
            redirectAttributes.addFlashAttribute("unpaidProductInBrand", true);
            redirectAttributes.addFlashAttribute("brandName", name);
            return "redirect:" + referer;
        } else {
            this.brandService.deleteByName(name);
            redirectAttributes.addFlashAttribute("successfullyDeleted", true);
            redirectAttributes.addFlashAttribute("brandName", name);
            return "redirect:" + referer;
        }
    }

    @GetMapping("/allByBrand/{name}")
    public String getAllProductsByCategory(@PathVariable String name, Model model){
        model.addAttribute("getAllProductsByBrand", this.brandService.getAllProducts(name));
        return "all-products-by-brands";
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
            return this.userService.getCartId();
        }
        return Long.valueOf(0);
    }
}
