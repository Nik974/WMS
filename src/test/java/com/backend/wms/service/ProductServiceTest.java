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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Test
    void addProduct_ShouldSaveAndReturnDto_WhenProductIsUnique() {
        Category category = new Category();
        category.setCategoryName("Electronics");
        category.setId(1L);

        ProductDto expectedDto = new ProductDto(1L, "Laptop", "LAP123", "test", 1L, "Electronics");

        CreateProductRequest request = new CreateProductRequest("Laptop", "LAP123", "test", 1L);

        Product product = new Product();
        product.setId(1L);
        product.setProductName("Laptop");
        product.setProductSku("LAP123");
        product.setUnit("test");
        product.setCategory(category);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setUnit("test");
        savedProduct.setProductName("Laptop");
        savedProduct.setProductSku("LAP123");
        savedProduct.setCategory(category);

        when(productRepository.findByProductSku("LAP123")).thenReturn(empty());
        when(productMapper.toEntity(request)).thenReturn(product);
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toDto(any(Product.class))).thenReturn(expectedDto);

        ProductDto result = productService.addProduct(request);
        assertEquals(expectedDto.productSku(), result.productSku());
    }

    @Test
    void addProduct_ShouldThrowException_WhenProductAlreadyExists() {
        CreateProductRequest request = new CreateProductRequest("Laptop", "LAP123", "test", 1L);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setProductName("Laptop");
        existingProduct.setProductSku("LAP123");
        existingProduct.setUnit("test");

        when(productRepository.findByProductSku("LAP123")).thenReturn(of(existingProduct));

        assertThrows(ResourceAlreadyExistsException.class, () -> productService.addProduct(request));
    }

    @Test
    void addProduct_ShouldThrowException_WhenCategoryDoesNotExist() {
        CreateProductRequest request = new CreateProductRequest("Laptop", "LAP123", "test", 1L);

        when(productRepository.findByProductSku("LAP123")).thenReturn(empty());

        Category proxyCategory = new Category();
        proxyCategory.setId(1L);
        when(categoryRepository.getReferenceById(1L)).thenReturn(proxyCategory);
        when(productMapper.toEntity(any())).thenReturn(new Product());

        when(productRepository.save(any(Product.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> productService.addProduct(request));
    }

    @Test
    void editProduct_ShouldUpdateAndReturnDto_WhenProductIsValid() {
        Long productId = 1L;
        CreateProductRequest request = new CreateProductRequest("Updated Laptop", "LAP123", "test", 1L);
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setProductName("Laptop");
        existingProduct.setProductSku("LAP123");
        existingProduct.setUnit("test");
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Electronics");
        existingProduct.setCategory(category);

        ProductDto expectedDto = new ProductDto(productId, "Updated Laptop", "LAP123", "test", 1L, "Electronics");

        when(productRepository.findById(productId)).thenReturn(of(existingProduct));

        when(categoryRepository.getReferenceById(1L)).thenReturn(category);
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(productMapper.toDto(existingProduct)).thenReturn(expectedDto);

        ProductDto result = productService.editProduct(productId, request);

        assertNotNull(result);
        assertEquals(productId, result.productId());
        assertEquals("Updated Laptop", result.productName());

    }

    @Test
    void editProduct_ShouldThrowException_WhenProductNotFound() {
        CreateProductRequest request = new CreateProductRequest("Laptop", "LAP123", "test", 1L);
        when(productRepository.findById(1L)).thenReturn(empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.editProduct(1L, request));
    }

    @Test
    void editProduct_ShouldThrowException_WhenNewSkuAlreadyExists() {
        Long productId = 1L;
        CreateProductRequest request = new CreateProductRequest("Updated", "NEW_SKU", "test", 1L);

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setProductSku("OLD_SKU");

        Product anotherProduct = new Product();

        when(productRepository.findById(productId)).thenReturn(of(existingProduct));
        when(productRepository.findByProductSku("NEW_SKU")).thenReturn(of(anotherProduct));

        assertThrows(ResourceAlreadyExistsException.class, () -> productService.editProduct(productId, request));
    }

    @Test
    void getProductById_ShouldReturnDto_WhenProductExists() {
        Product product = new Product();
        product.setId(1L);
        ProductDto expectedDto = new ProductDto(1L, "Laptop", "LAP123", "test", 1L, "Electronics");

        when(productRepository.findById(1L)).thenReturn(of(product));
        when(productMapper.toDto(product)).thenReturn(expectedDto);

        ProductDto result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }
    @Test
    void getAllProducts_ShouldReturnListOfDto() {
        List<Product> products = List.of(new Product(), new Product());
        List<ProductDto> dtos = List.of(
                new ProductDto(1L, "P1", "SKU1", "unit", 1L, "Cat1"),
                new ProductDto(2L, "P2", "SKU2", "unit", 1L, "Cat1")
        );

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toDtoList(products)).thenReturn(dtos);

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(2, result.size());
    }
}