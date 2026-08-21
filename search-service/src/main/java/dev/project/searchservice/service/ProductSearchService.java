package dev.project.searchservice.service;

import dev.project.searchservice.dto.ProductSearchResponse;
import dev.project.searchservice.mapper.ProductMapper;
import dev.project.searchservice.repository.ProductSearchRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

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

    public Optional<ProductSearchResponse> getProductById(String id){
        return searchRepository.findById(id).map(mapper::toResponse);
    }


}
