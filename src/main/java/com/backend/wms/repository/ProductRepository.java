package com.backend.wms.repository;

import com.backend.wms.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByProductNameContainingIgnoreCase(String name);

    Optional<Product> findByProductSku(String productSku);

    List<Product> findAllByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.category.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    List<Product> findAllByCategoryNameContainingIgnoreCase(String categoryName);

}
