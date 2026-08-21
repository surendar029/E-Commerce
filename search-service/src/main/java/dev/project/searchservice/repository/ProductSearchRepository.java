package dev.project.searchservice.repository;

import dev.project.searchservice.document.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument,String> {

    Page<ProductDocument> findByCategoryId(Long categoryId, Pageable pageable);

    Page<ProductDocument> findByCategoryName(String categoryName, Pageable pageable);

    Page<ProductDocument> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
