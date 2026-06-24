package com.electronics.controller;

import com.electronics.dto.checkout.CheckoutInitResponse;
import com.electronics.dto.checkout.ConfirmCheckoutRequest;
import com.electronics.dto.checkout.OrderResponse;
import com.electronics.service.CheckoutService;
import com.electronics.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/init")
    public ResponseEntity<CheckoutInitResponse> initCheckout() {
        Integer customerId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(checkoutService.initiateCheckout(customerId));
    }

    @PostMapping("/confirm")
    public ResponseEntity<OrderResponse> confirmCheckout(
        @Valid @RequestBody ConfirmCheckoutRequest request
    ) {
        Integer customerId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(checkoutService.confirmCheckout(customerId, request));
    }
}
