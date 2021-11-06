package com.example.minimarket.web;

import com.example.minimarket.model.bindings.CourierGetBindingModel;
import com.example.minimarket.model.entities.CourierEntity;
import com.example.minimarket.model.views.CartViewModel;
import com.example.minimarket.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/carts")
public class CartController {

    private final CourierService courierService;
    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;
    private final ProductService productService;

    public CartController(CourierService courierService, UserService userService, CartService cartService, OrderService orderService, ProductService productService) {
        this.courierService = courierService;
        this.userService = userService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.productService = productService;
    }


    @GetMapping("/view/{id}")
    public String viewCart(@PathVariable Long id, Model model){
        if(!model.containsAttribute("courierGetBindingModel")){
            model.addAttribute("courierGetBindingModel", new CourierGetBindingModel());
        }
        model.addAttribute("allOrders", this.orderService.findAllOrderByIsPaid(false, this.userService.getCartId()));
        model.addAttribute("getCartTotalPrice", this.userService.getTotalPriceForAllOrders());
        model.addAttribute("allCouriers", this.courierService.findAll());
        CartViewModel cartEntity = this.cartService.getCartById(id);
        CourierEntity courierEntity = this.cartService.getCartById(id).getCourier();
       if(this.cartService.getCartById(id).getCourier() != null && this.cartService.getCartById(id).getAddress() != null){
           model.addAttribute("cart", this.cartService.getCartById(id));
           return "view-final-cart";
       }
        if(this.cartService.getCartById(id).getCourier() != null){
            return "redirect:/addresses/add";
        }

        return "view-cart";
    }

    @GetMapping("/view")
    public String view(){
        return "redirect:/";
    }

    @GetMapping("/buy")
    public String buyCart(){
        this.cartService.updateCart(this.userService.getCartId());
        this.orderService.updateOrder(this.userService.getCartId());
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteCartById(@PathVariable Long id){
        this.cartService.updateCart(id);
        this.orderService.deleteAllCartOrders(id);

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
