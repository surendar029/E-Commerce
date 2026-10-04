package dev.project.productservice.service;


import dev.project.productservice.dto.ProductRequest;
import dev.project.productservice.dto.ProductResponse;
import dev.project.productservice.entity.CategoryEntity;
import dev.project.productservice.entity.ProductEntity;
import dev.project.productservice.event.ProductEvent;
import dev.project.productservice.exception.ProductAlreadyExistsException;
import dev.project.productservice.exception.ResourceNotFoundException;
import dev.project.productservice.producer.ProductEventProducer;
import dev.project.productservice.repository.CategoryRepository;
import dev.project.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEventProducer productEventProducer;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductEventProducer productEventProducer) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productEventProducer = productEventProducer;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.name()))
            throw new ProductAlreadyExistsException("Product with name '" + request.name() + "' already exists");

        CategoryEntity categoryEntity = categoryRepository.findById(request.categoryId()).orElseThrow(() ->
                new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));

        ProductEntity entity = mapToProductEntity(request, categoryEntity);
        ProductEntity saved = productRepository.save(entity);

        ProductEvent event = buildCreateEvent(saved, request.stockQuantity());
        productEventProducer.publishEvent(event);

        return mapToProductResponse(saved);
    }


    public ProductResponse getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Product not found with ID: " + id)
        );
        return mapToProductResponse(entity);
    }


    public List<ProductResponse> listProduct(){
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Product not found with ID: " + id)
        );

        if (request.name() != null) productEntity.setName(request.name());
        if (request.description() != null) productEntity.setDescription(request.description());
        if (request.price() != null) productEntity.setPrice(request.price());

        if (request.categoryId() != null) {
            CategoryEntity categoryEntity = categoryRepository.findById(request.categoryId()).orElseThrow(() ->
                    new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));
            productEntity.setCategoryEntity(categoryEntity);
        }

        ProductEntity entity = productRepository.save(productEntity);
        ProductEvent event = buildUpdateEvent(entity);
        productEventProducer.publishEvent(event);

        return mapToProductResponse(entity);
    }

    @Transactional
    public void deleteProduct(Long id) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Product not found with ID: " + id)
        );

        ProductEvent event = buildDeleteEvent(productEntity);
        productRepository.delete(productEntity);
        productEventProducer.publishEvent(event);
    }

    private ProductEvent buildDeleteEvent(ProductEntity product) {
        return ProductEvent.builder()
                .id(product.getId())
                .eventType(ProductEvent.EventType.DELETED)
                .build();
    }

    private ProductEvent buildUpdateEvent(ProductEntity product) {
        return ProductEvent.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategoryEntity().getId())
                .categoryName(product.getCategoryEntity().getName())
                .eventType(ProductEvent.EventType.UPDATED)
                .build();
    }

    private ProductEvent buildCreateEvent(ProductEntity product, Integer stock) {
        return ProductEvent.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(stock)
                .categoryId(product.getCategoryEntity().getId())
                .categoryName(product.getCategoryEntity().getName())
                .eventType(ProductEvent.EventType.CREATED)
                .build();
    }

    private ProductEntity mapToProductEntity(ProductRequest request, CategoryEntity categoryEntity) {
        return ProductEntity.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .categoryEntity(categoryEntity)
                .build();
    }

    private ProductResponse mapToProductResponse(ProductEntity product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategoryEntity().getId())
                .categoryName(product.getCategoryEntity().getName())
                .build();
    }
}
