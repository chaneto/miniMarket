package com.example.minimarket.web;

import com.example.minimarket.model.bindings.CourierAddBindingModel;
import com.example.minimarket.model.bindings.CourierGetBindingModel;
import com.example.minimarket.model.services.CourierServiceModel;
import com.example.minimarket.services.CourierService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/couriers")
public class CourierController {

    private final CourierService courierService;
    private final ModelMapper mapper;

    public CourierController(CourierService courierService, ModelMapper mapper, ModelMapper mapper1) {
        this.courierService = courierService;
        this.mapper = mapper1;
    }

    @GetMapping("/add")
    public String addCourier(Model model){
        if(!model.containsAttribute("courierAddBindingModel")){
            model.addAttribute("courierAddBindingModel", new CourierAddBindingModel());
            model.addAttribute("courierIsExists", false);
        }
        return "add-courier";
    }

    @PostMapping("/add")
    public String addCourierConfirm(@Valid CourierAddBindingModel courierAddBindingModel,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("courierAddBindingModel", courierAddBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.courierAddBindingModel", bindingResult);
            return "redirect:add";
        }

        CourierServiceModel courierServiceModel = this.mapper.map(courierAddBindingModel, CourierServiceModel.class);
        if(this.courierService.findByName(courierServiceModel.getName()) != null){
            redirectAttributes.addFlashAttribute("courierAddBindingModel", courierAddBindingModel);
            redirectAttributes.addFlashAttribute("courierIsExists", true);
            return "redirect:add";
        }
        this.courierService.saveCourier(courierServiceModel);
        redirectAttributes.addFlashAttribute("courierAddBindingModel", courierAddBindingModel);
        redirectAttributes.addFlashAttribute("successfulAddedCourier", true);
        return "redirect:add";
    }

    @GetMapping("/all")
    public String allCourier(Model model){
        model.addAttribute("allCourier", this.courierService.findAll());
        return "all-couriers";
    }

    @GetMapping("/delete/{name}")
    public String allCourier(@PathVariable String name){
        this.courierService.deleteByName(name);
        return "redirect:/";
    }

    @PostMapping("/get")
    public String view(@Valid CourierGetBindingModel courierGetBindingModel,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("courierGetBindingModel", courierGetBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.courierGetBindingModel", bindingResult);
            return "redirect:get";
        }
       this.courierService.setCourierCart(courierGetBindingModel.getName());
        return "redirect:/addresses/add";
    }

}
