package com.backend.wms.service;

import com.backend.wms.dto.CategoryDto;
import com.backend.wms.dto.CategoryRequest;
import com.backend.wms.entity.Category;
import com.backend.wms.exception.ResourceAlreadyExistsException;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.CategoryMapper;
import com.backend.wms.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setCategoryName("Electronics");
        category.setId(1L);
    }

    @Test
    void addCategory_ShouldSaveAndReturnDto_WhenCategoryIsUnique() {
        CategoryRequest request = new CategoryRequest("Electronics");
        Category entity = new Category();
        Category savedEntity = new Category();
        savedEntity.setId(1L);
        CategoryDto expectedDto = new CategoryDto(1L, "Electronics");

        when(categoryRepository.existsByCategoryNameIgnoreCase("Electronics")).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(savedEntity);
        when(categoryMapper.toDto(savedEntity)).thenReturn(expectedDto);

        CategoryDto result = categoryService.addCategory(request);

        assertNotNull(result);
        assertEquals(1L, result.categoryId());
        assertEquals("Electronics", result.categoryName());
    }

    @Test
    void addCategory_ShouldThrowException_WhenCategoryAlreadyExists() {
        CategoryRequest request = new CategoryRequest("Electronics");
        when(categoryRepository.existsByCategoryNameIgnoreCase("Electronics")).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class, () -> categoryService.addCategory(request));

        assertEquals("Category already exists", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void editCategory_ShouldUpdateAndReturnDto_WhenCategoryIsValid() {
        Long categoryId = 1L;
        CategoryRequest request = new CategoryRequest("Updated Electronics");
        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setCategoryName("Electronics");
        CategoryDto expectedDto = new CategoryDto(categoryId, "Updated Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByCategoryNameIgnoreCase("Updated Electronics")).thenReturn(false);
        when(categoryMapper.toDto(existingCategory)).thenReturn(expectedDto);

        CategoryDto result = categoryService.editCategory(categoryId, request);

        assertNotNull(result);
        assertEquals(categoryId, result.categoryId());
        assertEquals("Updated Electronics", result.categoryName());
        verify(categoryMapper).updateCategoryFromDto(request, existingCategory);
    }

    @Test
    void editCategory_ShouldThrowException_WhenCategoryNotFound() {
        Long categoryId = 1L;
        CategoryRequest request = new CategoryRequest("Updated Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> categoryService.editCategory(categoryId, request));

        assertEquals("Category not found", exception.getMessage());
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void getCategoryById_ShouldReturnDto_WhenCategoryExists() {

        Long categoryId = 1L;
        Category category = new Category();
        CategoryDto expectedDto = new CategoryDto(categoryId, "Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        CategoryDto result = categoryService.getCategoryById(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.categoryId());
    }

    @Test
    void getCategoryById_ShouldThrowException_WhenCategoryNotFound() {

        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
        verifyNoInteractions(categoryMapper);
    }
}