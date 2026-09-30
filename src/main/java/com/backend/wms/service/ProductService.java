package com.backend.wms.service;

import com.backend.wms.dto.CreateProductRequest;
import com.backend.wms.dto.ProductDto;
import com.backend.wms.entity.Category;
import com.backend.wms.entity.Product;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.ProductMapper;
import com.backend.wms.repository.CategoryRepository;
import com.backend.wms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toDtoList(products);
    }

    public List<ProductDto> getProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findAllByCategoryId(categoryId);
        return productMapper.toDtoList(products);
    }

    public List<ProductDto> getProductsByCategoryName(String categoryName) {
        List<Product> products = productRepository.findAllByCategoryNameContainingIgnoreCase(categoryName);
        return productMapper.toDtoList(products);
    }

    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toDto(product);
    }

    public ProductDto getProductBySku(String productSku) {
        Product product = productRepository.findByProductSku(productSku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found, sku: " + productSku));
        return productMapper.toDto(product);
    }
    
    public List<ProductDto> getProductsByName(String productName) {
        List<Product> products = productRepository.findAllByProductNameContainingIgnoreCase(productName);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found with name: " + productName);
        }
        return productMapper.toDtoList(products);
    }

    @Transactional
    public ProductDto addProduct(CreateProductRequest productDto) {
        if (productRepository.findByProductSku(productDto.productSku()).isPresent()) {
            throw new ResourceAlreadyExistsException("Product with SKU already exists: " + productDto.productSku());
        }
        Product product = productMapper.toEntity(productDto);
        Category category = categoryRepository.getReferenceById(productDto.categoryId());
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductDto editProduct(Long productId, CreateProductRequest productDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.getProductSku().equals(productDto.productSku()) && productRepository.findByProductSku(productDto.productSku()).isPresent()) {
            throw new ResourceAlreadyExistsException("Product with SKU already exists: " + productDto.productSku());
        }
        product.setProductName(productDto.productName());
        product.setProductSku(productDto.productSku());
        product.setUnit(productDto.unit());
        Category category = categoryRepository.getReferenceById(productDto.categoryId());
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

}