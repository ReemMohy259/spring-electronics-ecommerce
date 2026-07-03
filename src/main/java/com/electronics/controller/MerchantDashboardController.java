package com.electronics.controller;

import com.electronics.dto.DashboardStatsResponse;
import com.electronics.dto.PageResponse;
import com.electronics.dto.ProductRequest;
import com.electronics.dto.ProductResponse;
import com.electronics.dto.ProfileResponse;
import com.electronics.dto.checkout.OrderResponse;
import com.electronics.service.MerchantDashboardService;
import com.electronics.service.ProductService;
import com.electronics.service.ProfileService;
import com.electronics.util.CurrentUserDataUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
public class MerchantDashboardController {

    private final MerchantDashboardService merchantDashboardService;
    private final CurrentUserDataUtil currentUserDataUtil;
    private final ProductService productService;
    private final ProfileService profileService;

    @GetMapping("/dashboard")
    public DashboardStatsResponse getDashboard() {
        String keycloakId = currentUserDataUtil.getCurrentUser().keycloakId();
        return merchantDashboardService.getDashboard(keycloakId);
    }

    @GetMapping("/products")
    public PageResponse<ProductResponse> getProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        String keycloakId = currentUserDataUtil.getCurrentUser().keycloakId();
        return merchantDashboardService.getProducts(keycloakId, pageable);
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @GetMapping("/orders")
    public PageResponse<OrderResponse> getOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        String keycloakId = currentUserDataUtil.getCurrentUser().keycloakId();
        return merchantDashboardService.getOrders(keycloakId, pageable);
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile() {
        return profileService.getCurrentProfile();
    }
}
