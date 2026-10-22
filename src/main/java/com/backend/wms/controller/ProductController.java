package com.backend.wms.controller;

import com.backend.wms.dto.CreateProductRequest;
import com.backend.wms.dto.ProductDto;
import com.backend.wms.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    // GET /api/v1/products
    @GetMapping
    public ResponseEntity<List<ProductDto>> listAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/v1/products/123
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // GET /api/v1/products/sku/ABC-1234
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDto> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    // GET /api/v1/products/category/5
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> listProductsByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    // GET /api/v1/products/category/name/electronics
    @GetMapping("/category/name/{categoryName}")
    public ResponseEntity<List<ProductDto>> listProductsByCategory(@PathVariable String categoryName) {
        return ResponseEntity.ok(productService.getProductsByCategoryName(categoryName));
    }

    // GET /api/v1/products/search?query=
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.getProductsByName(query));
    }

    // POST /api/v1/products
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody CreateProductRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(dto));
    }

    // PUT /api/v1/products/123
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProductDto> editProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest dto) {
        return ResponseEntity.ok(productService.editProduct(id, dto));
    }
}
