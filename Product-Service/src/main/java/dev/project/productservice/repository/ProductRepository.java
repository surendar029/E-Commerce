package dev.project.productservice.repository;

import dev.project.productservice.dto.ProductResponse;
import dev.project.productservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<ProductEntity,Long> {

    boolean existsByName(String name);

}
