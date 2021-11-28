package com.example.minimarket.web;

import com.example.minimarket.model.views.ProductViewModel;
import com.example.minimarket.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/products")
@RestController
public class ProductRestController {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductRestController(ProductRepository productRepository,
                               ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/api")
    public ResponseEntity<List<ProductViewModel>> findAll() {

        List<ProductViewModel> productViewModels = productRepository.
                findAll().
                stream().
                map(p -> {
                    ProductViewModel productViewModel = modelMapper.map(p, ProductViewModel.class);
                    productViewModel.setCategory(p.getCategory().getName());
                    productViewModel.setBrand(p.getBrand().getName());
                    return productViewModel;
                }).
                collect(Collectors.toList());

        return ResponseEntity
                .ok()
                .body(productViewModels);
    }
}
