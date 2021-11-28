package com.example.minimarket.web;

import com.example.minimarket.model.bindings.ProductGetBuyQuantity;
import com.example.minimarket.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CarouselService carouselService;
    private final ProductService  productService;
    private final UserService userService;

    public HomeController(CarouselService carouselService, ProductService productService, UserService userService) {
        this.carouselService = carouselService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Authentication authentication, Model model){
        model.addAttribute("carouselImages", carouselService.getImages());
        model.addAttribute("allProducts", this.productService.findAllOrderByName());
        model.addAttribute("promotionsProducts", this.productService.getPromotionProduct());
        if(authentication == null){
        return "index";
        }

       model.addAttribute("orderCount", this.userService.getCountAllUserOrders());
       model.addAttribute("getTotalPriceForAllOrders", this.userService.getTotalPriceForAllOrders());
       model.addAttribute("getCardId", this.userService.getCartId());
       if(!model.containsAttribute("productGetBuyQuantity")){
           model.addAttribute("productGetBuyQuantity", new ProductGetBuyQuantity());
       }
       return "home";

     }

     @GetMapping("/contacts")
    public String contact(){
        return "contacts";
     }

}
