package com.backend.wms.controller;

import com.backend.wms.dto.CategoryDto;
import com.backend.wms.dto.CategoryRequest;
import com.backend.wms.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // GET /api/v1/categories
    @GetMapping
    public ResponseEntity<List<CategoryDto>> listAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // GET /api/v1/categories/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryByID(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // POST /api/v1/categories
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CategoryDto> addNewCategory(@Valid @RequestBody CategoryRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(dto));
    }

    // PUT /api/v1/categories/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CategoryDto> editCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest dto) {
        return ResponseEntity.ok(categoryService.editCategory(id, dto));
    }
}
