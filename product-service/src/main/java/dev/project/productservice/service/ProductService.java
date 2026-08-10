package dev.project.productservice.service;


import dev.project.productservice.config.KafkaProducerConfig;
import dev.project.productservice.dto.ProductPageResponse;
import dev.project.productservice.dto.ProductRequest;
import dev.project.productservice.dto.ProductResponse;
import dev.project.productservice.entity.CategoryEntity;
import dev.project.productservice.entity.ProductEntity;
import dev.project.productservice.event.ProductEvent;
import dev.project.productservice.exception.ProductAlreadyExistsException;
import dev.project.productservice.exception.ResourceNotFoundException;
import dev.project.productservice.repository.CategoryRepository;
import dev.project.productservice.repository.ProductRepository;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = KafkaProducerConfig.PRODUCT_EVENTS_TOPIC;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @CacheEvict(value = {"products", "productList"}, allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.name()))
            throw new ProductAlreadyExistsException("Product with name '" + request.name() + "' already exists");

        CategoryEntity category = categoryRepository.findById(request.categoryId()).orElseThrow(() ->
                new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));

        ProductEntity productEntity = new ProductEntity(
                category,
                request.description(),
                request.name(),
                request.price(),
                request.stockQuantity()
        );
        ProductEntity saved = productRepository.save(productEntity);
        kafkaTemplate.send(TOPIC, saved.getId().toString(), mapToEvent(saved, ProductEvent.EventType.CREATED));

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Product not found with ID: " + id));
        return mapToResponse(entity);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "productList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public ProductPageResponse getAllProducts(Pageable pageable) {
        Page<ProductResponse> page = productRepository.findAll(pageable).map(this::mapToResponse);
        return new ProductPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long id, Pageable pageable) {
        if (!categoryRepository.existsById(id))
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        return productRepository.findByCategoryEntityId(id, pageable).map(this::mapToResponse);
    }


    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "products", key = "#id"),
                    @CacheEvict(value = "productList", allEntries = true)
            }
    )
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Product not found with ID: " + id));
        CategoryEntity categoryEntity = categoryRepository.findById(request.categoryId()).orElseThrow(() ->
                new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));

        productEntity.setName(request.name());
        productEntity.setDescription(request.description());
        productEntity.setPrice(request.price());
        productEntity.setStockQuantity(request.stockQuantity());
        productEntity.setCategoryEntity(categoryEntity);

        ProductEntity updated = productRepository.save(productEntity);
        kafkaTemplate.send(TOPIC, updated.getId().toString(), mapToEvent(updated, ProductEvent.EventType.UPDATED));

        return mapToResponse(updated);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "products", key = "#id"),
                    @CacheEvict(value = "productList", allEntries = true)
            }
    )
    public void deleteProduct(Long id) {
        ProductEntity deleted = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        productRepository.delete(deleted);
        kafkaTemplate.send(TOPIC, deleted.getId().toString(), mapToEvent(deleted, ProductEvent.EventType.DELETED));
    }


    private ProductEvent mapToEvent(ProductEntity product, ProductEvent.EventType eventType) {
        return new ProductEvent(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategoryEntity().getId(),
                product.getCategoryEntity().getName(),
                eventType
        );
    }

    private ProductResponse mapToResponse(ProductEntity entity) {
        return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStockQuantity(),
                entity.getCategoryEntity().getId(),
                entity.getCategoryEntity().getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
