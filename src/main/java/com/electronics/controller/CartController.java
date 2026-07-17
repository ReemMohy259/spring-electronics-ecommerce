package com.electronics.controller;

import com.electronics.dto.cart.AddToCartRequest;
import com.electronics.dto.cart.CartResponse;
import com.electronics.dto.cart.UpdateCartItemRequest;
import com.electronics.service.CartService;
import com.electronics.util.CurrentUserDataUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CurrentUserDataUtil currentUserDataUtil;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Integer customerId = currentUserDataUtil.getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addCartItem(@RequestBody AddToCartRequest request) {
        Integer customerId = currentUserDataUtil.getCurrentUserId();
        return ResponseEntity.ok(cartService.addToCart(customerId, request));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
        @PathVariable Integer productId,
        @Valid @RequestBody UpdateCartItemRequest request) {
        Integer customerId = currentUserDataUtil.getCurrentUserId();
        return ResponseEntity.ok(cartService.updateItem(customerId, productId, request));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Integer productId) {
        Integer customerId = currentUserDataUtil.getCurrentUserId();
        return ResponseEntity.ok(cartService.removeItem(customerId, productId));
    }
}
