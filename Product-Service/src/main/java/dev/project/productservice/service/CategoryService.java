package dev.project.productservice.service;

import dev.project.productservice.dto.CategoryListResponse;
import dev.project.productservice.dto.CategoryRequest;
import dev.project.productservice.dto.CategoryResponse;
import dev.project.productservice.entity.CategoryEntity;
import dev.project.productservice.exception.CategoryAlreadyExistsException;
import dev.project.productservice.exception.ResourceNotFoundException;
import dev.project.productservice.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name()))
            throw new CategoryAlreadyExistsException("Category with name '" + request.name() + "' already exists");

        CategoryEntity entity = CategoryEntity.builder()
                .name(request.name())
                .description(request.description())
                .build();
        CategoryEntity saved = categoryRepository.save(entity);

        return mapToResponse(saved);
    }

    public CategoryResponse getCategoryByID(Long id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapToResponse(entity);
    }

    public CategoryListResponse getAllCategory() {
        return new CategoryListResponse(categoryRepository.findAll().stream().map(this::mapToResponse).toList());
    }

    public CategoryResponse mapToResponse(CategoryEntity entity) {
        return new CategoryResponse(entity.getId(), entity.getName(), entity.getDescription());
    }
}
