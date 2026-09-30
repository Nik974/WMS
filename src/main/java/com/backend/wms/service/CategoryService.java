package com.backend.wms.service;

import com.backend.wms.dto.CategoryDto;
import com.backend.wms.dto.CategoryRequest;
import com.backend.wms.entity.Category;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.CategoryMapper;
import com.backend.wms.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public boolean existsByCategoryNameIgnoreCase(String categoryName) {
        return categoryRepository.existsByCategoryNameIgnoreCase(categoryName);
    }

    public List<CategoryDto> getAllCategories() {
        return categoryMapper.toDtoList(categoryRepository.findAll());
    }

    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.toDto(category);
    }

    @Transactional
    public CategoryDto addCategory(CategoryRequest categoryDto) {
        if (existsByCategoryNameIgnoreCase(categoryDto.categoryName())) {
            throw new ResourceAlreadyExistsException("Category already exists");}

        Category entity = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(entity);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public CategoryDto editCategory(Long categoryId, CategoryRequest categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        boolean isCategoryNameChanged = !category.getCategoryName().equalsIgnoreCase(categoryDto.categoryName());
        if (isCategoryNameChanged && existsByCategoryNameIgnoreCase(categoryDto.categoryName())) {
            throw new ResourceAlreadyExistsException("Category already exists" + categoryDto.categoryName());
        }

        categoryMapper.updateCategoryFromDto(categoryDto, category);
        return categoryMapper.toDto(category);
    }
}
