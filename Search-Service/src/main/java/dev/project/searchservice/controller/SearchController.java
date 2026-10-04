package dev.project.searchservice.controller;

import dev.project.searchservice.document.ProductDocument;
import dev.project.searchservice.repository.ProductSearchRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequestMapping("/test")
@RestController
public class SearchController {

    private final ProductSearchRepository repository;

    public SearchController(ProductSearchRepository repository) {
        this.repository = repository;
    }


}
