package com.backend.wms.controller;

import com.backend.wms.dto.CategoryDto;
import com.backend.wms.dto.CategoryRequest;
import com.backend.wms.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDto>> listAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryByID(@RequestBody Long id){
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CategoryDto> addNewCategory(@Valid @RequestBody CategoryRequest dto){
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(categoryService.addCategory(dto));
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CategoryDto> editCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest dto){
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(categoryService.editCategory(id, dto));
    }
}
