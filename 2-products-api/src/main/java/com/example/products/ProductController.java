package com.example.products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductController {

    // Only these columns may be used in ?sort= (unknown fields get a clear 400 instead of a 500)
    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "name", "category", "price");

    private final ProductRepository products;

    public ProductController(ProductRepository products) {
        this.products = products;
    }

    // GET /products?category=electronics&minPrice=50&maxPrice=500&page=0&size=5&sort=price,desc&sort=name,asc
    // Spring builds the Pageable from the page, size and sort query params (sort can repeat for multiple fields).
    @GetMapping
    public Page<Product> list(@RequestParam(required = false) String category,
                              @RequestParam(required = false) BigDecimal minPrice,
                              @RequestParam(required = false) BigDecimal maxPrice,
                              @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot sort by '" + order.getProperty() + "'. Allowed: " + SORTABLE_FIELDS);
            }
        }
        String categoryFilter = category == null ? null : category.toLowerCase();
        return products.search(categoryFilter, minPrice, maxPrice, pageable);
    }
}
