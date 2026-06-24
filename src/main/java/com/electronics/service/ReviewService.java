package com.electronics.service;

import com.electronics.dto.CreateReviewRequest;
import com.electronics.dto.ReviewResponse;
import com.electronics.dto.UpdateReviewRequest;
import com.electronics.entity.Product;
import com.electronics.entity.Review;
import com.electronics.entity.User;
import com.electronics.exception.InvalidRequestException;
import com.electronics.exception.ProductReviewedException;
import com.electronics.exception.ReviewNotFoundException;
import com.electronics.exception.UserNotFoundException;
import com.electronics.exception.product.ProductNotFoundException;
import com.electronics.repository.ProductRepository;
import com.electronics.repository.ReviewRepository;
import com.electronics.repository.UserRepository;
import com.electronics.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addReview(CreateReviewRequest request) {

        String email = SecurityUtil.getCurrentUserEmail();

        if (reviewRepository.findByUser_EmailAndProduct_Id(email, request.getProductId())
            .isPresent()) {
            throw new ProductReviewedException(email, request.getProductId());
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        reviewRepository.save(review);
    }

    public Page<ReviewResponse> getProductReviews(Integer productId, Pageable pageable) {
        return reviewRepository.findAllByProduct_Id(productId, pageable)
            .map(
                r -> new ReviewResponse(
                    r.getId(),
                    r.getUser().getEmail(), // TODO:UPDATE IT TO BE USERNAME
                    r.getProduct().getId(),
                    r.getRating(),
                    r.getComment()));
    }

    public void deleteReview(Integer productId) {
        String email = SecurityUtil.getCurrentUserEmail();

        Optional<Review> reviewOpt = reviewRepository
            .findByUser_EmailAndProduct_Id(email, productId);
        if (reviewOpt.isEmpty()) {
            throw new ReviewNotFoundException(email, productId);
        }

        if (!reviewOpt.get().getUser().getEmail().equals(email)) {
            throw new InvalidRequestException("Not allowed, ownership required");
        }

        reviewRepository.delete(reviewOpt.get());
    }

    public void updateReview(UpdateReviewRequest request) {

        String email = SecurityUtil.getCurrentUserEmail();

        Optional<Review> reviewOpt = reviewRepository
            .findByUser_EmailAndProduct_Id(email, request.getProductId());
        if (reviewOpt.isEmpty()) {
            throw new ReviewNotFoundException(email, request.getProductId());
        }

        if (!reviewOpt.get().getUser().getEmail().equals(email)) {
            throw new InvalidRequestException("Not allowed, ownership required");
        }

        Review review = reviewOpt.get();

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        reviewRepository.save(review);
    }
}
