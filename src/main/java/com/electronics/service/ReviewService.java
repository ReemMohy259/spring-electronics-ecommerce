package com.electronics.service;

import com.electronics.dto.CreateReviewRequest;
import com.electronics.dto.ReviewResponse;
import com.electronics.entity.Product;
import com.electronics.entity.Review;
import com.electronics.entity.User;
import com.electronics.exception.product.ProductNotFoundException;
import com.electronics.exception.ProductReviewedException;
import com.electronics.exception.UserNotFoundException;
import com.electronics.repository.UserRepository;
import com.electronics.repository.ProductRepository;
import com.electronics.repository.ReviewRepository;
import com.electronics.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addReview(CreateReviewRequest request) {

        String email = SecurityUtil.getCurrentUserEmail();

        if (reviewRepository.findByUser_EmailAndProduct_Id(email, request.productId())
                .isPresent()) {
            throw new ProductReviewedException(email, request.productId());
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.rating());
        review.setComment(request.comment());

        System.out.println("Test Review" + review);

        reviewRepository.save(review);
    }

    public Page<ReviewResponse> getProductReviews(Integer productId, Pageable pageable) {
        return reviewRepository.findAllByProduct_Id(productId, pageable)
                .map(r -> new ReviewResponse(r.getId(), r.getUser().getUsername(),
                        r.getProduct().getId(), r.getRating(), r.getComment()));
    }
}
