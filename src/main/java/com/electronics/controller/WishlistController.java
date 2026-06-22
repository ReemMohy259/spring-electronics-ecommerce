package com.electronics.controller;

import com.electronics.dto.AddWishlistRequest;
import com.electronics.dto.WishlistResponse;
import com.electronics.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public Page<WishlistResponse> getWishlist(Pageable pageable) {
        return wishlistService.getWishlist(pageable);
    }

    @PostMapping
    public void add(@Valid @RequestBody AddWishlistRequest request) {
        wishlistService.addToWishlist(request);
    }

    @DeleteMapping("/{productId}")
    public void remove(@PathVariable Integer productId) {
        wishlistService.removeFromWishlist(productId);
    }
}
