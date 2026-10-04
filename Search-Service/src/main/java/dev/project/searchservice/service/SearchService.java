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
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class SearchService {
    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchService(ProductSearchRepository productSearchRepository, ElasticsearchOperations elasticsearchOperations) {
        this.productSearchRepository = productSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public ProductSearchResponse getProductById(Long productId) {
        ProductDocument document = productSearchRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product not found for Product ID: " + productId));
        return buildResponse(document);
    }

    public Page<ProductSearchResponse> searchProducts(
            String query,
            Long categoryId,
            String categoryName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        Criteria criteria = new Criteria();

        if (query != null && !query.isBlank()) {
            String trimmed = query.trim();
            Criteria textCriteria = new Criteria("name").contains(trimmed)
                    .or(new Criteria("description").contains(trimmed));
            criteria = criteria.and(textCriteria);
        }

        if (categoryId != null) criteria = criteria.and(new Criteria("categoryId").is(categoryId));

        if (categoryName != null && !categoryName.trim().isEmpty())
            criteria = criteria.and(new Criteria("categoryName").is(categoryName.trim()));

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidPriceRangeException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        if (minPrice != null && maxPrice != null) {
            criteria = criteria.and(new Criteria("price").between(minPrice, maxPrice));
        } else if (minPrice != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(minPrice));
        } else if (maxPrice != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(maxPrice));
        }

        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria).setPageable(pageable);
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(criteriaQuery, ProductDocument.class);

        List<ProductSearchResponse> result = searchHits.stream()
                .map(SearchHit::getContent)
                .map(this::buildResponse)
                .toList();

        return new PageImpl<>(result, pageable, searchHits.getTotalHits());
    }


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

    public void deleteProduct(Long productId) {
        productSearchRepository.deleteById(productId);
    }

    public ProductSearchResponse buildResponse(ProductDocument document) {
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
