package com.example.minimarket.web;

import com.example.minimarket.model.bindings.OrdersByDateTimeBindingModel;
import com.example.minimarket.model.bindings.UserDeleteBindingModel;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.services.CartService;
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
import java.time.LocalDate;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final ModelMapper mapper;

    public OrderController(OrderService orderService, UserService userService, CartService cartService, ModelMapper mapper) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.mapper = mapper;
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, Model model, HttpServletRequest request){
        String referer = request.getHeader("Referer");
        CartServiceModel currentCart = this.userService.getCurrentCart();
        if(this.orderService.findOrderById(id) != null){
        boolean isOrdered = this.orderService.findOrderById(id).isOrdered();
        if(this.orderService.findOrderById(id).getCart().getId().equals(currentCart.getId()) && !isOrdered){
        BigDecimal orderPrice = this.orderService.findOrderById(id).getTotalPrice();
        CartServiceModel orderCart = this.mapper.map(this.orderService.findOrderById(id).getCart(), CartServiceModel.class);
        this.orderService.deleteOrderById(id);
          if(orderCart.getTotalPrice().compareTo(BigDecimal.valueOf(0)) > 0){
                this.cartService.setTotalPrice(orderCart.getTotalPrice().subtract(orderPrice),orderCart.getId());
            }
          }
        }
        updateDataCurrentCart(model, currentCart);
        if(this.userService.getCurrentCart().getCourier() != null){
             return "view-final-cart";
        }else {
        return "redirect:"+ referer;
        }
    }

    @GetMapping("/deleteByAdminPanel/{id}")
    public String deleteByAdminPanel(@PathVariable Long id, Model model){
        if(this.orderService.findOrderById(id) != null
                && this.userService.getCurrentUser().getRole().getUserRole().name().equals("ADMIN")){
        BigDecimal orderPrice = this.orderService.findOrderById(id).getTotalPrice();
        CartServiceModel orderCart = this.mapper.map(this.orderService.findOrderById(id).getCart(), CartServiceModel.class);
        boolean isOrdered = this.orderService.findOrderById(id).isOrdered();
        this.orderService.deleteOrderById(id);

        if(orderCart.getTotalPrice().compareTo(BigDecimal.valueOf(0)) > 0 && !isOrdered){
            this.cartService.setTotalPrice(orderCart.getTotalPrice().subtract(orderPrice),orderCart.getId());
           }
        updateDataCurrentCart(model, this.userService.getCurrentCart());
        }
       getUserDeleteAndOrdersByDateTimeBindingModel(model);
        getMaxDateAndAllUsersAttribute(model);
        model.addAttribute("orders", this.orderService.findAllOrdersOrderByDateTime());
        return "all-orders";
    }


    @GetMapping("/history")
    public String orderHistory(Model model){
        model.addAttribute("ordersHistory", this.orderService.findAllByCartId(this.userService.getCurrentCartId()));
        return "order-history";
    }

    @GetMapping("/all")
    public String getAllIsNoPaidOrders(Model model){
        getUserDeleteAndOrdersByDateTimeBindingModel(model);
        getMaxDateAndAllUsersAttribute(model);
        model.addAttribute("orders", this.orderService.findAllOrdersOrderByDateTime());
        return "all-orders";
    }

    @GetMapping("/allDelivered")
    public String getAllDeliveredOrders(Model model){
       getUserDeleteAndOrdersByDateTimeBindingModel(model);
       getMaxDateAndAllUsersAttribute(model);
        model.addAttribute("orders", this.orderService.findAllByIsDeliveredOrderByDateTime(true));
        return "all-orders";
    }

    @GetMapping("/allNotDelivered")
    public String getAllIsNotDeliveredOrders(Model model){
       getUserDeleteAndOrdersByDateTimeBindingModel(model);
        getMaxDateAndAllUsersAttribute(model);
        model.addAttribute("orders", this.orderService.findAllByIsDeliveredOrderByDateTime(false));
        return "all-orders";
    }

    @PostMapping("/selectByUser")
    public String selectByUser(@Valid UserDeleteBindingModel userDeleteBindingModel, BindingResult bindingResult
                               , RedirectAttributes redirectAttributes
                               , Model model){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("userDeleteBindingModel", userDeleteBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDeleteBindingModel", bindingResult);
            return "redirect:/orders/all";
        } else if(this.userService.userWithUsernameIsExists(userDeleteBindingModel.getUsername())){
            getMaxDateAndAllUsersAttribute(model);
            model.addAttribute("ordersByDateTimeBindingModel", new OrdersByDateTimeBindingModel());
            model.addAttribute("orders", this.orderService.findAllByCartId
                    (this.userService.findByUsernameEntity(userDeleteBindingModel.getUsername()).getCart().getId()));
            return "all-orders";
        } else {
            redirectAttributes.addFlashAttribute("userDeleteBindingModel", userDeleteBindingModel);
            redirectAttributes.addFlashAttribute("userNotExists", true);
            return "redirect:/orders/all";
        }
    }

    @PostMapping("/selectByDate")
    public String selectByDate(@Valid OrdersByDateTimeBindingModel ordersByDateTimeBindingModel
            , BindingResult bindingResult, RedirectAttributes redirectAttributes
            , Model model){
        getMaxDateAndAllUsersAttribute(model);
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("ordersByDateTimeBindingModel", ordersByDateTimeBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.ordersByDateTimeBindingModel", bindingResult);
            return "redirect:/orders/all";
        }
        model.addAttribute("userDeleteBindingModel", new UserDeleteBindingModel());
        model.addAttribute("orders", this.orderService.findAllOrdersByDate(ordersByDateTimeBindingModel.getDate()));
        return "all-orders";
    }

    public void getMaxDateAndAllUsersAttribute(Model model){
        model.addAttribute("maxDate", LocalDate.now());
        model.addAttribute("allUsernames", this.userService.findAllUsername());
    }

    public void getUserDeleteAndOrdersByDateTimeBindingModel(Model model){
        if(!model.containsAttribute("userDeleteBindingModel")){
            model.addAttribute("userDeleteBindingModel", new UserDeleteBindingModel());
        }
        if(!model.containsAttribute("ordersByDateTimeBindingModel")){
            model.addAttribute("ordersByDateTimeBindingModel", new OrdersByDateTimeBindingModel());
        }
    }

    public void updateDataCurrentCart(Model model, CartServiceModel currentCart){
        currentCart = this.userService.getCurrentCart();
        model.addAttribute("cart", currentCart);
        model.addAttribute("allOrders", this.orderService.findAllOrderByIsOrderedAndCartId(false, currentCart.getId()));
        model.addAttribute("orderCount", this.userService.getCountAllUserOrders());
        model.addAttribute("getTotalPriceForAllOrders", this.userService.getTotalPriceForAllOrders());
        model.addAttribute("getCardId", this.userService.getCurrentCartId());
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
