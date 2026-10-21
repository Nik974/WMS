package com.backend.wms.controller;

import com.backend.wms.dto.CreateProductRequest;
import com.backend.wms.dto.ProductDto;
import com.backend.wms.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> listAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductDto>> listProductsByCategory(@PathVariable @RequestBody String categoryName){
        return ResponseEntity.ok(productService.getProductsByCategoryName(categoryName));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> listProductsByCategoryId(@PathVariable @RequestBody Long categoryId){
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDto> getProductBySku(@PathVariable @RequestBody String sku){
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable @RequestBody Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/all/{query}")
    public ResponseEntity<List<ProductDto>> searchProducts(@PathVariable @RequestBody String query){
        return ResponseEntity.ok(productService.getProductsByName(query));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody CreateProductRequest dto){
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(productService.addProduct(dto));
    }

    @PutMapping("/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProductDto> editProduct(@Valid @RequestBody Long id, CreateProductRequest dto){
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(productService.editProduct(id, dto));
    }
}
