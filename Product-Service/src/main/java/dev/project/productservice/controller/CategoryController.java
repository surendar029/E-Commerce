package dev.project.productservice.controller;


import dev.project.productservice.dto.CategoryListResponse;
import dev.project.productservice.dto.CategoryRequest;
import dev.project.productservice.dto.CategoryResponse;
import dev.project.productservice.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCategory(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryByID(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getCategoryByID(id));
    }

    @GetMapping
    public ResponseEntity<CategoryListResponse> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllCategory());
    }
}
