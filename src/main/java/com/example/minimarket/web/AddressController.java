package com.example.minimarket.web;

import com.example.minimarket.model.bindings.AddressAddBindingModel;
import com.example.minimarket.model.services.AddressServiceModel;
import com.example.minimarket.services.AddressService;
import com.example.minimarket.services.OrderService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public AddressController(AddressService addressService, ModelMapper mapper, UserService userService) {
        this.addressService = addressService;
        this.mapper = mapper;
        this.userService = userService;
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
        model.addAttribute("cart", this.userService.getCurrentCart());
        model.addAttribute("allOrders", this.userService.getAllUserOrderByIsPaid(false, this.userService.getCartId()));
        return "view-final-cart";
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
