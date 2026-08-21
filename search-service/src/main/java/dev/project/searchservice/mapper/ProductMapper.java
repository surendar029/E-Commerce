package dev.project.searchservice.mapper;


import dev.project.searchservice.document.ProductDocument;
import dev.project.searchservice.dto.ProductSearchResponse;
import dev.project.searchservice.event.ProductEvent;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDocument toDocument(ProductEvent event) {
        if (event == null) return null;
        return new ProductDocument(
                String.valueOf(event.id()),
                event.name(),
                event.description(),
                event.price(),
                event.stockQuantity(),
                event.categoryId(),
                event.categoryName()
        );
    }

    public ProductSearchResponse toResponse(ProductDocument document) {
        if (document == null) return null;

        return new ProductSearchResponse(
                document.getId(),
                document.getName(),
                document.getDescription(),
                document.getPrice(),
                document.getStockQuantity(),
                document.getCategoryId(),
                document.getCategoryName()
        );
    }
}
