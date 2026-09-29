package com.backend.wms.mapper;

import com.backend.wms.dto.CategoryDto;
import com.backend.wms.dto.CategoryRequest;
import com.backend.wms.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "id", target = "categoryId")
    CategoryDto toDto(Category entity);

    List<CategoryDto> toDtoList(List<Category> entities);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(target = "id", ignore = true)
    void updateCategoryFromDto(CategoryRequest request, @MappingTarget Category entity);

}
