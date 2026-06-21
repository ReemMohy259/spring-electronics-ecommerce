package com.electronics.controller;

import com.electronics.dto.CreateReviewRequest;
import com.electronics.dto.ReviewResponse;
import com.electronics.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping()
    public void addReview(@Valid @RequestBody CreateReviewRequest request) {
        reviewService.addReview(request);
    }

    @GetMapping("/products/{productId}")
    public Page<ReviewResponse> getReviews(@PathVariable Integer productId, Pageable pageable) {
        return reviewService.getProductReviews(productId, pageable);
    }
}