package com.electronics.controller;

import com.electronics.dto.cart.AddToCartRequest;
import com.electronics.dto.cart.CartItemResponse;
import com.electronics.dto.cart.CartResponse;
import com.electronics.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Integer id = SecurityUtil.getCurrentUserId();
        return null;
    }

    @PostMapping
    public ResponseEntity<CartResponse> addCartItem(
        @RequestBody AddToCartRequest addToCartRequest
    ) {

    }
}
