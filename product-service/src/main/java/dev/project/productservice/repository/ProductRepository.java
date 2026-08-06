package dev.project.productservice.repository;

import dev.project.productservice.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByName(String name);
    Page<ProductEntity> findByCategoryEntityId(Long categoryId, Pageable pageable);
}
