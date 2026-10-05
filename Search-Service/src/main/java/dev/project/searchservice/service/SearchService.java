package dev.project.searchservice.service;

import dev.project.searchservice.document.ProductDocument;
import dev.project.searchservice.dto.ProductSearchResponse;
import dev.project.searchservice.event.ProductEvent;
import dev.project.searchservice.exception.InvalidPriceRangeException;
import dev.project.searchservice.exception.ResourceNotFoundException;
import dev.project.searchservice.repository.ProductSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchService(
            ProductSearchRepository productSearchRepository,
            ElasticsearchOperations elasticsearchOperations
    ) {
        this.productSearchRepository = productSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    // --------------------------------------------------
    // Get product by ID
    // --------------------------------------------------

    public ProductSearchResponse getProductById(Long productId) {
        ProductDocument document = productSearchRepository
                .findById(productId).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found for Product ID: " + productId));
        return buildResponse(document);
    }

    // --------------------------------------------------
    // Search products
    // --------------------------------------------------

    public Page<ProductSearchResponse> searchProducts(
            String query,
            Long categoryId,
            String categoryName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {

        // Validate price range
        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            throw new InvalidPriceRangeException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(bool -> {

                    // ------------------------------------------
                    // Keyword search
                    // name OR description
                    // ------------------------------------------

                    if (query != null && !query.isBlank()) {

                        bool.must(m -> m.multiMatch(mm -> mm
                                .query(query.trim())
                                .fields("name", "description")
                        ));
                    }

                    // ------------------------------------------
                    // Category ID filter
                    // ------------------------------------------

                    if (categoryId != null) {

                        bool.filter(f -> f.term(t -> t
                                .field("categoryId")
                                .value(categoryId)
                        ));
                    }

                    // ------------------------------------------
                    // Category name filter
                    // ------------------------------------------

                    if (categoryName != null
                            && !categoryName.isBlank()) {

                        bool.filter(f -> f.term(t -> t
                                .field("categoryName")
                                .value(categoryName.trim())
                        ));
                    }

                    // ------------------------------------------
                    // Minimum price
                    // price >= minPrice
                    // ------------------------------------------

                    if (minPrice != null) {

                        bool.filter(f -> f.range(r -> r
                                .number(n -> n
                                        .field("price")
                                        .gte(minPrice.doubleValue())
                                )
                        ));
                    }

                    // ------------------------------------------
                    // Maximum price
                    // price <= maxPrice
                    // ------------------------------------------

                    if (maxPrice != null) {

                        bool.filter(f -> f.range(r -> r
                                .number(n -> n
                                        .field("price")
                                        .lte(maxPrice.doubleValue())
                                )
                        ));
                    }

                    return bool;
                }))
                .withPageable(pageable)
                .build();

        // Execute Elasticsearch query
        SearchHits<ProductDocument> searchHits =
                elasticsearchOperations.search(
                        searchQuery,
                        ProductDocument.class
                );

        // Convert Elasticsearch documents
        // to API response objects
        List<ProductSearchResponse> result =
                searchHits.stream()
                        .map(SearchHit::getContent)
                        .map(this::buildResponse)
                        .toList();

        // Return paginated response
        return new PageImpl<>(
                result,
                pageable,
                searchHits.getTotalHits()
        );
    }

    // --------------------------------------------------
    // Index / update product
    // --------------------------------------------------

    public void indexProduct(ProductEvent event) {

        ProductDocument product = ProductDocument.builder()
                .id(event.id())
                .name(event.name())
                .description(event.description())
                .price(event.price())
                .categoryId(event.categoryId())
                .categoryName(event.categoryName())
                .build();

        productSearchRepository.save(product);
    }

    // --------------------------------------------------
    // Delete product
    // --------------------------------------------------

    public void deleteProduct(Long productId) {

        productSearchRepository.deleteById(productId);
    }

    // --------------------------------------------------
    // Build API response
    // --------------------------------------------------

    public ProductSearchResponse buildResponse(
            ProductDocument document
    ) {

        return ProductSearchResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .price(document.getPrice())
                .categoryId(document.getCategoryId())
                .category(document.getCategoryName())
                .build();
    }
}