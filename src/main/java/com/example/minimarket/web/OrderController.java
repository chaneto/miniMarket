package com.example.minimarket.web;

import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;

    public OrderController(OrderService orderService, UserService userService, CartService cartService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, Model model, HttpServletRequest request){
        String referer = request.getHeader("Referer");

        BigDecimal orderPrice = this.orderService.findOrderById(id).getTotalPrice();
        CartServiceModel currentCart = this.userService.getCurrentCart();
        this.orderService.deleteOrderById(id);
        this.cartService.setTotalPrice(currentCart.getTotalPrice().subtract(orderPrice),currentCart.getId());
        currentCart = this.userService.getCurrentCart();
        model.addAttribute("cart", currentCart);
        model.addAttribute("allOrders", this.orderService.findAllOrderByIsPaidAndCartId(false, currentCart.getId()));
        model.addAttribute("orderCount", this.userService.getCountAllUserOrders());
        model.addAttribute("getTotalPriceForAllOrders", this.userService.getTotalPriceForAllOrders());
        model.addAttribute("getCardId", this.userService.getCartId());
        if(this.userService.getCurrentCart().getCourier() != null){
             return "view-final-cart";
        }else {
        return "redirect:"+ referer;

        }

    }

    @GetMapping("/history")
    public String orderHistory(Model model){
        model.addAttribute("ordersHistory", this.orderService.findAllByCartId(this.userService.getCartId()));
        return "order-history";
    }

}
