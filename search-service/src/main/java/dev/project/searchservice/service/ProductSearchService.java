package dev.project.searchservice.service;

import dev.project.searchservice.document.ProductDocument;
import dev.project.searchservice.dto.ProductSearchResponse;
import dev.project.searchservice.mapper.ProductMapper;
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
import java.util.Optional;

@Service
public class ProductSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductSearchRepository searchRepository;
    private final ProductMapper mapper;

    public ProductSearchService(ElasticsearchOperations elasticsearchOperations, ProductSearchRepository searchRepository, ProductMapper mapper) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.searchRepository = searchRepository;
        this.mapper = mapper;
    }

    public Optional<ProductSearchResponse> getProductById(String id) {
        return searchRepository.findById(id).map(mapper::toResponse);
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

        if (query != null && !query.trim().isEmpty()) {
            String trimmed = query.trim();
            Criteria textCriteria = new Criteria("name").contains(trimmed).
                    or(new Criteria("description").contains(trimmed));
            criteria = criteria.and(textCriteria);
        }

        if (categoryId != null) criteria = criteria.and(new Criteria("categoryId").is(categoryId));

        if (categoryName != null && !categoryName.trim().isEmpty()) {
            criteria = criteria.and(new Criteria("categoryName").is(categoryName.trim()));
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

        List<ProductSearchResponse> results = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(mapper::toResponse)
                .toList();


        return new PageImpl<>(results, pageable, searchHits.getTotalHits());
    }
}
