package com.electronics.controller;

import com.electronics.dto.CategoryRequest;
import com.electronics.dto.AdminDashboardStatsResponse;
import com.electronics.dto.AdminUserResponse;
import com.electronics.dto.CategoryResponse;
import com.electronics.dto.PageResponse;
import com.electronics.dto.ProductResponse;
import com.electronics.service.AdminService;
import com.electronics.service.CategoryService;
import com.electronics.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final AdminService adminService;

    @GetMapping("/dashboard/stats")
    public AdminDashboardStatsResponse getDashboardStats() {
        return adminService.getDashboardStats();
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getUsers(@RequestParam String role) {
        return adminService.getUsers(role);
    }

    @GetMapping("/products")
    public PageResponse<ProductResponse> findAllProducts(
        @PageableDefault(size = 20) Pageable pageable) {
        return productService.findAll(null, null, null, null, pageable);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Integer id) {
        productService.softDelete(id);
    }

    @GetMapping("/categories")
    public PageResponse<CategoryResponse> findAllCategories(
        @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    @DeleteMapping("/customers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Integer id) {
        adminService.softDeleteCustomer(id);
    }

    @DeleteMapping("/merchants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMerchant(@PathVariable Integer id) {
        adminService.softDeleteMerchant(id);
    }

    @PatchMapping("/orders/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Integer id) {
        adminService.cancelOrder(id);
    }
}
