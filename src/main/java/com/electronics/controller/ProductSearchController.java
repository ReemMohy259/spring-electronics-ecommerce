package com.electronics.controller;

import com.electronics.dto.ProductSearchRequest;
import com.electronics.dto.ProductSearchResponse;
import com.electronics.service.ProductIndexService;
import com.electronics.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;
    private final ProductIndexService productIndexService;

    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponse> search(ProductSearchRequest request) {
        return ResponseEntity.ok(productSearchService.search(request));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(@RequestParam String query) {
        return ResponseEntity.ok(productSearchService.autocomplete(query));
    }

    @PostMapping("/search/reindex")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reindex() {
        productIndexService.reindexAll();
        return ResponseEntity.ok().build();
    }
}
