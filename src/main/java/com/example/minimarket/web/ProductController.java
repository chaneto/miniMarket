package com.example.minimarket.web;

import com.example.minimarket.model.bindings.ProductAddBindingModel;
import com.example.minimarket.model.bindings.ProductAddQuantityBindingModel;
import com.example.minimarket.model.bindings.ProductGetBuyQuantity;
import com.example.minimarket.model.services.ProductServiceModel;
import com.example.minimarket.services.BrandService;
import com.example.minimarket.services.CategoryService;
import com.example.minimarket.services.ProductService;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ModelMapper mapper;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final UserService userService;

    public ProductController(ProductService productService, ModelMapper mapper, CategoryService categoryService, BrandService brandService, UserService userService) {
        this.productService = productService;
        this.mapper = mapper;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.userService = userService;
    }

    @GetMapping("/add")
    private String addProduct(Model model){
        if(!model.containsAttribute("productAddBindingModel")){
            model.addAttribute("productAddBindingModel", new ProductAddBindingModel());
            model.addAttribute("productIsExists", false);
            model.addAttribute("allCategory", this.categoryService.getAllCategoryName());
            model.addAttribute("allBrands", this.brandService.getAllBrands());
        }
        return "add-product";
    }

    @PostMapping("/add")
    public String addProductConfirm(@Valid ProductAddBindingModel productAddBindingModel,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("productAddBindingModel", productAddBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productAddBindingModel", bindingResult);
            return "redirect:add";
        }
        ProductServiceModel productServiceModel = this.mapper.map(productAddBindingModel, ProductServiceModel.class);
        if(this.productService.findByName(productServiceModel.getName()) != null){
            redirectAttributes.addFlashAttribute("productAddBindingModel", productAddBindingModel);
            redirectAttributes.addFlashAttribute("productIsExists", true);
            return "redirect:add";
        }

        this.productService.seedProduct(productServiceModel);
        return "redirect:/";
    }

    @GetMapping("/all")
    public String allProducts(Model model){
        model.addAttribute("allProducts", this.productService.findAll());
        return "all-products";
    }

    @GetMapping("/delete/{name}")
    public String deleteProduct(@PathVariable String name){
        this.productService.deleteProduct(name);
        return "redirect:/";
    }
    @GetMapping("/addQuantity")
        public String AddQuantity(Model model){
        if(!model.containsAttribute("productAddQuantityBindingModel")){
            model.addAttribute("productAddQuantityBindingModel", new ProductAddQuantityBindingModel());
            model.addAttribute("successfullyAddedQuantity", false);
        }
        model.addAttribute("allProductsName", this.productService.getAllProductsName());
            return "add-quantity";
        }

    @PostMapping("/addQuantity")
    public String addQuantity(ProductAddQuantityBindingModel productAddQuantityBindingModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes, Model model){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("productAddQuantityBindingModel", productAddQuantityBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productAddQuantityBindingModel", bindingResult);
            return "redirect:addQuantity";
        }

        this.productService.addQuantity(productAddQuantityBindingModel.getQuantity(), productAddQuantityBindingModel.getName());
        redirectAttributes.addFlashAttribute("productAddQuantityBindingModel", productAddQuantityBindingModel);
        redirectAttributes.addFlashAttribute("successfullyAddedQuantity", true);
        return "redirect:addQuantity";
    }

    @PostMapping("/buy/{name}")
    //@RequestMapping(value = "/buy/{name}", method = RequestMethod.POST)
    public String getBuyQuantity( @Valid ProductGetBuyQuantity productGetBuyQuantity,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes, @PathVariable String name, Model model,
                                  HttpServletRequest request){
        String referer = request.getHeader("Referer");
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("productGetBuyQuantity",productGetBuyQuantity);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productGetBuyQuantity", bindingResult);
            return "redirect:"+ referer;
        }
        if(this.productService.quantityIsEnough(name, productGetBuyQuantity.getQuantity())){
            this.productService.buyProduct(name, productGetBuyQuantity.getQuantity());
            return "redirect:"+ referer;
        }else {
            redirectAttributes.addFlashAttribute("quantityIsNotEnough",true);
            redirectAttributes.addFlashAttribute("productName",name);
            return "redirect:"+ referer;
        }
    }

    @GetMapping("/allByCategory/{name}")
    public String getAllProductsByCategory(@PathVariable String name, Model model){
        model.addAttribute("getAllProductsByCategory", this.productService.findAllByCategory(name));
        return "all-products-by-category";
    }

    @GetMapping("/details/{name}")
    public String details(@PathVariable String name, Model model){
        model.addAttribute("product", this.productService.findByName(name));
        return "details";
    }

    @ModelAttribute("productGetBuyQuantity")
    public ProductGetBuyQuantity productGetBuyQuantity(){
        return new ProductGetBuyQuantity();
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
