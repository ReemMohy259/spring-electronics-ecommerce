package com.electronics.controller;

import com.electronics.dto.CreateReviewRequest;
import com.electronics.dto.ReviewResponse;
import com.electronics.dto.UpdateReviewRequest;
import com.electronics.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public void addReview(
        @PathVariable Integer productId,
        @Valid @RequestBody CreateReviewRequest request) {
        request.setProductId(productId);
        reviewService.addReview(request);
    }

    @GetMapping
    public Page<ReviewResponse> getReviews(@PathVariable Integer productId, Pageable pageable) {
        return reviewService.getProductReviews(productId, pageable);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public void deleteReviews(@PathVariable Integer productId) {
        reviewService.deleteReview(productId);
    }

    @PatchMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public void updateReview(
        @PathVariable Integer productId,
        @Valid @RequestBody UpdateReviewRequest request) {
        request.setProductId(productId);
        reviewService.updateReview(request);
    }
}