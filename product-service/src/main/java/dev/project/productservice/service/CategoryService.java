package dev.project.productservice.service;

import dev.project.productservice.dto.CategoryRequest;
import dev.project.productservice.dto.CategoryResponse;
import dev.project.productservice.entity.CategoryEntity;
import dev.project.productservice.exception.CategoryAlreadyExistsException;
import dev.project.productservice.exception.ResourceNotFoundException;
import dev.project.productservice.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name()))
            throw new CategoryAlreadyExistsException("Category with name '" + request.name() + "' already exists");

        CategoryEntity entity = new CategoryEntity(request.name(), request.description());
        CategoryEntity saved = categoryRepository.save(entity);
        return mapToResponse(saved);
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getCategoryById(Long id) {
        CategoryEntity entity = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapToResponse(entity);
    }

    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::mapToResponse).toList();
    }


    public CategoryResponse mapToResponse(CategoryEntity entity) {
        return new CategoryResponse(entity.getId(), entity.getName(), entity.getDescription());
    }
}
