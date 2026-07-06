package com.electronics.controller;

import com.electronics.dto.MerchantResponse;
import com.electronics.dto.PageResponse;
import com.electronics.dto.ProductRequest;
import com.electronics.dto.ProductResponse;
import com.electronics.service.MerchantService;
import com.electronics.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final MerchantService merchantService;

    @GetMapping("/featured")
    public PageResponse<ProductResponse> findFeatured(
        @PageableDefault(size = 20) Pageable pageable) {
        return productService.findFeatured(pageable);
    }

    @GetMapping
    public PageResponse<ProductResponse> findAll(
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) String keyword,
        @PageableDefault(size = 20) Pageable pageable) {
        return productService.findAll(categoryId, minPrice, maxPrice, keyword, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Integer id) {
        return productService.findById(id);
    }

    @GetMapping("/merchants/{merchantId}")
    public MerchantResponse findMerchantDetails(@PathVariable Integer merchantId) {
        return merchantService.findById(merchantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // @PreAuthorize("hasAnyRole('ADMIN', 'MERCHANT')")
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ProductResponse update(
        @PathVariable Integer id,
        @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MERCHANT')")
    public void softDelete(@PathVariable Integer id) {
        productService.softDelete(id);
    }
}
