package dev.project.searchservice.controller;

import dev.project.searchservice.dto.ProductSearchResponse;
import dev.project.searchservice.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Search and filter products in Elasticsearch index.
     * Supports full-text search (query), filtering by categoryId, categoryName, and price range, with pagination.
     *
     * @param query        Optional search keyword matching product name or description
     * @param categoryId   Optional filter by category ID
     * @param categoryName Optional filter by category name
     * @param minPrice     Optional minimum price boundary
     * @param maxPrice     Optional maximum price boundary
     * @param pageable     Pagination and sorting information (default size 10)
     * @return Paginated list of matching products
     */
    @GetMapping({"", "/products"})
    public ResponseEntity<Page<ProductSearchResponse>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ProductSearchResponse> products = searchService.searchProducts(
                query,
                categoryId,
                categoryName,
                minPrice,
                maxPrice,
                pageable
        );
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieve a single product from the search index by its ID.
     *
     * @param id The product ID
     * @return Product details
     */
    @GetMapping({"/{id}", "/products/{id}"})
    public ResponseEntity<ProductSearchResponse> getProductById(@PathVariable Long id) {
        ProductSearchResponse product = searchService.getProductById(id);
        return ResponseEntity.ok(product);
    }
}
