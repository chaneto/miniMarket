package com.example.minimarket.web;

import com.example.minimarket.model.bindings.AddressAddBindingModel;
import com.example.minimarket.model.services.AddressServiceModel;
import com.example.minimarket.services.AddressService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;
    private final ModelMapper mapper;
    private final UserService userService;
    private final OrderService orderService;

    public AddressController(AddressService addressService, ModelMapper mapper, UserService userService, OrderService orderService) {
        this.addressService = addressService;
        this.mapper = mapper;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/add")
    public String addAddress(Model model){
        if(!model.containsAttribute("addressAddBindingModel")){
            model.addAttribute("addressAddBindingModel", new AddressAddBindingModel());
        }
        return "add-address";
    }

    @PostMapping("/add")
    public String addAddressConfirm(@Valid AddressAddBindingModel addressAddBindingModel,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes, Model model){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("addressAddBindingModel", addressAddBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addressAddBindingModel", bindingResult);
            return "redirect:add";
        }
        AddressServiceModel addressServiceModel = this.mapper.map(addressAddBindingModel, AddressServiceModel.class);
        this.addressService.save(addressServiceModel);
        model.addAttribute("cart", this.userService.getCurrentUser().getCart());
        model.addAttribute("allOrders", this.orderService.findAllOrderByIsPaid(false, this.userService.getCartId()));
        return "view-final-cart";
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
