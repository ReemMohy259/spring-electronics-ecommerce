package com.electronics.service;

import com.electronics.dto.CreateReviewRequest;
import com.electronics.dto.ReviewResponse;
import com.electronics.dto.ReviewSummaryResponse;
import com.electronics.dto.ReviewSummaryResponse.RatingBreakdownResponse;
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
import com.electronics.util.CurrentUserDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CurrentUserDataUtil currentUserDataUtil;

    public void addReview(CreateReviewRequest request) {

        String email = currentUserDataUtil.getCurrentUserEmail();

        if (reviewRepository.findByUser_EmailAndProduct_Id(email, request.getProductId())
            .isPresent()) {
            throw new ProductReviewedException(email, request.getProductId());
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        userRepository.save(user);

        Review review = new Review();
        review.setUser(user);
        review.setReviewDisplayedName(
            currentUserDataUtil.getCurrentUser().firstName()
                + currentUserDataUtil.getCurrentUser().lastName());
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
                    r.getReviewDisplayedName(), // TODO:UPDATE IT TO BE USERNAME
                    r.getProduct().getId(),
                    r.getRating(),
                    r.getComment()));
    }

    public ReviewSummaryResponse getProductReviewsSummary(Integer productId) {
        productRepository.findByIdAndDeletedFalse(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        Double average = reviewRepository.findAverageRatingByProductId(productId);
        Long totalReviews = reviewRepository.countByProduct_Id(productId);
        Map<Integer, Long> countsByStars = reviewRepository.findRatingBreakdownByProductId(productId)
            .stream()
            .collect(Collectors.toMap(
                row -> ((Number) row[0]).intValue(),
                row -> ((Number) row[1]).longValue()));

        return new ReviewSummaryResponse(
            average == null ? 0.0 : average,
            totalReviews,
            IntStream.rangeClosed(1, 5)
                .mapToObj(stars -> new RatingBreakdownResponse(
                    stars,
                    countsByStars.getOrDefault(stars, 0L)))
                .toList());
    }

    public void deleteReview(Integer productId) {
        String email = currentUserDataUtil.getCurrentUserEmail();

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

        String email = currentUserDataUtil.getCurrentUserEmail();

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
