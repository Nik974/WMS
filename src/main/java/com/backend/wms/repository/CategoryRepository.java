package com.backend.wms.repository;

import com.backend.wms.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
