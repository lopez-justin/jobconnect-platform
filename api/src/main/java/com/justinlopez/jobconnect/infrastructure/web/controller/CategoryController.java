package com.justinlopez.jobconnect.infrastructure.web.controller;

import com.justinlopez.jobconnect.application.dto.response.CategoryResponse;
import com.justinlopez.jobconnect.application.service.ListCategoriesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ListCategoriesUseCase listCategoriesUseCase;

    @Operation(
            summary = "List all categories",
            description = "Returns all available job categories."
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        List<CategoryResponse> categories = this.listCategoriesUseCase.execute();
        return ResponseEntity.ok(categories);
    }

}
