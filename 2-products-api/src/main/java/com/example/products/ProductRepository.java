package com.example.products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Each filter is optional: if a parameter is null its condition is skipped.
    // category must already be lowercase (the controller does this) for a case-insensitive match.
    // Spring Data adds LIMIT/OFFSET (page, size) and ORDER BY (sort) from the Pageable automatically.
    @Query("""
            SELECT p FROM Product p
            WHERE (:category IS NULL OR LOWER(p.category) = :category)
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> search(String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
