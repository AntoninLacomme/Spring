package com.example.msproduct.controllers;

import com.example.msproduct.domain.Product;
import com.example.msproduct.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @GetMapping ("/products")
    public List<Product> list () {
        return productRepository.findAll();
    }

    @GetMapping ("/product/{id}")
    public Optional<Product> get (@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find the specified product");
        }
        return product;
    }

    @PostMapping ("/product/listid")
    public List<Product> getProductsById (@RequestBody List<Long> listIds) {
        List<Product> products = productRepository.findAllById(listIds);
        return products;
    }
}
