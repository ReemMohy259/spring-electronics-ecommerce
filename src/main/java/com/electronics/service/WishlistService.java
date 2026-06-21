package com.electronics.service;

import com.electronics.dto.AddWishlistRequest;
import com.electronics.dto.ProductResponse;
import com.electronics.dto.WishlistResponse;
import com.electronics.entity.Product;
import com.electronics.entity.User;
import com.electronics.entity.Wishlist;
import com.electronics.exception.ProductNotFoundException;
import com.electronics.exception.UserNotFoundException;
import com.electronics.repository.ProductRepository;
import com.electronics.repository.UserRepository;
import com.electronics.repository.WishlistRepository;
import com.electronics.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addToWishlist(AddWishlistRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();

        if (wishlistRepository.findByUser_EmailAndProduct_Id(email, request.productId())
                .isPresent()) {
            return; // already exists
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        wishlistRepository.save(wishlist);
    }

    public void removeFromWishlist(Integer productId) {
        String email = SecurityUtil.getCurrentUserEmail();

        wishlistRepository.findByUser_EmailAndProduct_Id(email, productId)
                .ifPresent(wishlistRepository::delete);
    }

    public Page<WishlistResponse> getWishlist(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserEmail();

        return wishlistRepository.findAllByUser_Email(email, pageable)
                .map(w -> new WishlistResponse(ProductResponse.from(w.getProduct())));
    }
}
