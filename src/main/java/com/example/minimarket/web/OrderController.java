package com.example.minimarket.web;

import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.services.CartService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        boolean isPaid = this.orderService.findOrderById(id).isPaid();
        if(this.orderService.findOrderById(id).getCart().getId().equals(currentCart.getId())){
        this.orderService.deleteOrderById(id);
        }
        if(currentCart.getTotalPrice().compareTo(BigDecimal.valueOf(0)) > 0 && !isPaid){
        this.cartService.setTotalPrice(currentCart.getTotalPrice().subtract(orderPrice),currentCart.getId());
        }
        currentCart = this.userService.getCurrentCart();
        model.addAttribute("cart", currentCart);
        model.addAttribute("allOrders", this.orderService.findAllOrderByIsOrderedAndCartId(false, currentCart.getId()));
        model.addAttribute("orderCount", this.userService.getCountAllUserOrders());
        model.addAttribute("getTotalPriceForAllOrders", this.userService.getTotalPriceForAllOrders());
        model.addAttribute("getCardId", this.userService.getCurrentCartId());
        if(this.userService.getCurrentCart().getCourier() != null){
             return "view-final-cart";
        }else {
        return "redirect:"+ referer;

        }

    }

    @GetMapping("/history")
    public String orderHistory(Model model){
        model.addAttribute("ordersHistory", this.orderService.findAllByCartId(this.userService.getCurrentCartId()));
        return "order-history";
    }

    @GetMapping("/all")
    public String getAllIsNoPaidOrders(Model model){
        model.addAttribute("orders", this.orderService.findAllOrdersOrderByDateTime());
        return "all-orders";
    }

    @GetMapping("/allDelivered")
    public String getAllDeliveredOrders(Model model){
        model.addAttribute("orders", this.orderService.findAllByIsDeliveredOrderByDateTime(true));
        return "all-orders";
    }

    @GetMapping("/allNotDelivered")
    public String getAllIsNotDeliveredOrders(Model model){
        model.addAttribute("orders", this.orderService.findAllByIsDeliveredOrderByDateTime(false));
        return "all-orders";
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
