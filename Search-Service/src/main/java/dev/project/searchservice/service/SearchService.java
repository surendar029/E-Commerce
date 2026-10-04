package dev.project.searchservice.service;


import dev.project.searchservice.document.ProductDocument;
import dev.project.searchservice.dto.ProductSearchResponse;
import dev.project.searchservice.event.ProductEvent;
import dev.project.searchservice.repository.ProductSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


@Service
public class SearchService {
    private final ProductSearchRepository productSearchRepository;

    public SearchService(ProductSearchRepository productSearchRepository) {
        this.productSearchRepository = productSearchRepository;
    }

    public

//    public Page<ProductSearchResponse> searchProducts(
//
//    ){
//
//    }


    public void indexProduct(ProductEvent event){
        ProductDocument product=ProductDocument.builder()
                .id(event.id())
                .name(event.name())
                .description(event.description())
                .price(event.price())
                .categoryId(event.categoryId())
                .category(event.categoryName())
                .build();
        productSearchRepository.save(product);
    }

    public void delete(Long event){
        productSearchRepository.deleteById(event);
    }
}
