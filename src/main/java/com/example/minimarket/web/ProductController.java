package com.example.minimarket.web;

import com.example.minimarket.model.bindings.*;
import com.example.minimarket.model.services.ProductServiceModel;
import com.example.minimarket.services.*;
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

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ModelMapper mapper;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;

    public ProductController(ProductService productService, ModelMapper mapper, CategoryService categoryService, BrandService brandService, UserService userService, CartService cartService, OrderService orderService) {
        this.productService = productService;
        this.mapper = mapper;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.userService = userService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    String oldPage = "";

    @GetMapping("/add")
    private String addProduct(Model model) {
        if (!model.containsAttribute("productAddBindingModel")) {
            model.addAttribute("productAddBindingModel", new ProductAddBindingModel());
            model.addAttribute("productIsExists", false);
        }
        model.addAttribute("allCategory", this.categoryService.getAllCategoryName());
        model.addAttribute("allBrands", this.brandService.getAllBrands());
        return "add-product";
    }

    @PostMapping("/add")
    public String addProductConfirm(@Valid ProductAddBindingModel productAddBindingModel,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("productAddBindingModel", productAddBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productAddBindingModel", bindingResult);
            return "redirect:add";
        }
        ProductServiceModel productServiceModel = this.mapper.map(productAddBindingModel, ProductServiceModel.class);
        if (this.productService.findByName(productServiceModel.getName()) != null) {
            redirectAttributes.addFlashAttribute("productAddBindingModel", productAddBindingModel);
            redirectAttributes.addFlashAttribute("productIsExists", true);
            return "redirect:add";
        }

        this.productService.seedProduct(productServiceModel);
        redirectAttributes.addFlashAttribute("productAddBindingModel", productAddBindingModel);
        redirectAttributes.addFlashAttribute("successfulProductAdded", true);
        return "redirect:add";
    }

    @GetMapping("/all")
    public String allProducts(Model model) {
        model.addAttribute("allProducts", this.productService.findAllOrderByName());
        return "all-products";
    }

    @GetMapping("/delete/{name}")
    public String deleteProduct(@PathVariable String name, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        if(orderService.productInUnpaidOrder(name)){
            redirectAttributes.addFlashAttribute("productInUnpaidOrder", true);
            redirectAttributes.addFlashAttribute("productName", name);
            return "redirect:" + referer;
        } else {
            redirectAttributes.addFlashAttribute("productName", name);
        this.productService.deleteProductByName(name);
            redirectAttributes.addFlashAttribute("deleteProduct", true);
            return "redirect:" + referer;
        }

    }

    @GetMapping("/setProductPrice/{id}")
    public String setPrice(@PathVariable Long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        model.addAttribute("product", this.productService.getById(id));
        String referer = request.getHeader("Referer");
        if (!model.containsAttribute("productSetPriceBindingModel")) {
            model.addAttribute("productSetPriceBindingModel", new ProductSetPriceBindingModel());
            model.addAttribute("successfullyChangedPrice", false);
            oldPage = referer;
        }
        model.addAttribute("allProductsName", this.productService.getAllProductsName());
        return "set-product-price";
    }

    @PostMapping("/setProductPrice")
    public String setPriceConfirm(@Valid  ProductSetPriceBindingModel productSetPriceBindingModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("productSetPriceBindingModel", productSetPriceBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productSetPriceBindingModel", bindingResult);
            return "redirect:" + referer;
        }

        this.productService.setPrice(productSetPriceBindingModel.getNewPrice(), productSetPriceBindingModel.getName());
        redirectAttributes.addFlashAttribute("productSetPriceBindingModel", productSetPriceBindingModel);
        redirectAttributes.addFlashAttribute("successfullyChangedPrice", true);
        return "redirect:" + oldPage;
    }

    @GetMapping("/setDiscountRate/{id}")
    public String setDiscountRate(@PathVariable Long id, Model model){
        if(!model.containsAttribute("productSetDiscountRateBindingModel")){
            model.addAttribute("productSetDiscountRateBindingModel", new ProductSetDiscountRateBindingModel());
            model.addAttribute("successfullyChangedDiscountRate", false);
        }

        model.addAttribute("allProductsName", this.productService.getAllProductsName());
        model.addAttribute("productName", this.productService.getById(id).getName());
        return "set-discount-rate";
    }

    @PostMapping("/setDiscountRate")
        public String setDiscountRateConfirm(@Valid ProductSetDiscountRateBindingModel productSetDiscountRateBindingModel,
                BindingResult bindingResult,
                RedirectAttributes redirectAttributes,
                HttpServletRequest request){
        String referer = request.getHeader("Referer");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("productSetDiscountRateBindingModel", productSetDiscountRateBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productSetDiscountRateBindingModel", bindingResult);
            return "redirect:" + referer;
        }
        this.productService.setPromotionPriceAndDiscountRate(productSetDiscountRateBindingModel.getDiscountRate(), productSetDiscountRateBindingModel.getName());
        redirectAttributes.addFlashAttribute("productSetDiscountRateBindingModel", productSetDiscountRateBindingModel);
        redirectAttributes.addFlashAttribute("successfullyChangedDiscountRate", true);
        return "redirect:/products/productsQuantity";
    }

    @GetMapping("/addQuantity/{name}")
    public String addQuantity(Model model, @PathVariable String name) {
        if (!model.containsAttribute("productAddQuantityBindingModel")) {
            model.addAttribute("productAddQuantityBindingModel", new ProductAddQuantityBindingModel());
            model.addAttribute("successfullyAddedQuantity", false);
        }
        model.addAttribute("allProductsName", this.productService.getAllProductsName());
        model.addAttribute("productName", name);
        return "add-quantity";
    }

    @PostMapping("/addQuantity")
    public String addQuantity(@Valid ProductAddQuantityBindingModel productAddQuantityBindingModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("productAddQuantityBindingModel", productAddQuantityBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productAddQuantityBindingModel", bindingResult);
            return "redirect:" + referer;
        }
        this.productService.addQuantity(productAddQuantityBindingModel.getQuantity(), productAddQuantityBindingModel.getName());
        redirectAttributes.addFlashAttribute("productAddQuantityBindingModel", productAddQuantityBindingModel);
        redirectAttributes.addFlashAttribute("successfullyAddedQuantity", true);
        redirectAttributes.addFlashAttribute("productName", productAddQuantityBindingModel.getName());
        redirectAttributes.addFlashAttribute("productQuantity", productAddQuantityBindingModel.getQuantity());
        return "redirect:/products/productsQuantity";
    }

    @PostMapping("/addProduct/{name}")
    public String addProductToCart(@Valid ProductGetBuyQuantity productGetBuyQuantity,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes, @PathVariable String name,
                                   HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("productGetBuyQuantity", productGetBuyQuantity);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.productGetBuyQuantity", bindingResult);
            return "redirect:" + referer;
        }
        BigDecimal quantity = productGetBuyQuantity.getQuantity();
        if(quantity == null){
            quantity = BigDecimal.valueOf(1);
        }
        if (this.productService.quantityIsEnough(name, quantity)) {
            this.cartService.addProductToCart(name, quantity, this.userService.getCartId());
            return "redirect:" + referer;
        } else {
            redirectAttributes.addFlashAttribute("quantityIsNotEnough", true);
            redirectAttributes.addFlashAttribute("productName", name);
            return "redirect:" + referer;
        }
    }

    @GetMapping("/details/{name}")
    public String details(@PathVariable String name, Model model) {
        model.addAttribute("product", this.productService.findByName(name));
        return "details";
    }

    @GetMapping("/promotion")
    public String promotionProducts(Model model) {
        model.addAttribute("promotionsProducts", this.productService.getPromotionProduct());
        return "promotional-products";
    }

    @GetMapping("/productsQuantity")
    public String seeProductsQuantities(Model model){
        model.addAttribute("productsQuantities", this.productService.findAllOrderByQuantity());
        return "products-quantities";
    }

    @ModelAttribute("productGetBuyQuantity")
    public ProductGetBuyQuantity productGetBuyQuantity() {
        return new ProductGetBuyQuantity();
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
