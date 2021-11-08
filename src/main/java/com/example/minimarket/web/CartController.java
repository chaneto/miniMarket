package com.example.minimarket.web;

import com.example.minimarket.model.bindings.CourierGetBindingModel;
import com.example.minimarket.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/carts")
public class CartController {

    private final CourierService courierService;
    private final UserService userService;
    private final CartService cartService;

    public CartController(CourierService courierService, UserService userService, CartService cartService) {
        this.courierService = courierService;
        this.userService = userService;
        this.cartService = cartService;
    }


    @GetMapping("/view/{id}")
    public String viewCart(@PathVariable Long id, Model model, HttpServletRequest request) {
        if(!model.containsAttribute("courierGetBindingModel")){
            model.addAttribute("courierGetBindingModel", new CourierGetBindingModel());
        }
        model.addAttribute("allOrders", this.userService.getAllUserOrderByIsPaid(false, id));
        model.addAttribute("getCartTotalPrice", this.userService.getTotalPriceForAllOrders());
        model.addAttribute("allCouriers", this.courierService.findAll());
        String referer = request.getHeader("Referer");
//        if(orderCount() == 0){
//            model.addAttribute("emptyCart", true);
//            return "view-cart";
//        }
        if(this.cartService.getCartById(id).getCourier() != null && this.cartService.getCartById(id).getAddress() != null){
           model.addAttribute("cart", this.cartService.getCartById(id));
           return "view-final-cart";
       }
       else if(this.cartService.getCartById(id).getCourier() != null){
            return "redirect:/addresses/add";
        }else {
        return "view-cart";
       }
    }



    @GetMapping("/view")
    public String view(){
        return "redirect:/";
    }

    @GetMapping("/buy")
    public String buyCart(){
        this.cartService.resetCart(this.userService.getCartId());
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteCartById(@PathVariable Long id){
        this.cartService.deleteCartById(id);
        return "redirect:/";
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
