package com.electronics.controller;

import com.electronics.dto.checkout.CheckoutInitResponse;
import com.electronics.dto.checkout.CheckoutStatusResponse;
import com.electronics.service.CheckoutService;
import com.electronics.util.CurrentUserDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final CurrentUserDataUtil currentUserDataUtil;

    @PostMapping("/init")
    public ResponseEntity<CheckoutInitResponse> initCheckout() {
        Integer customerId = currentUserDataUtil.getCurrentUserId();
        return ResponseEntity.ok(checkoutService.initiateCheckout(customerId));
    }

    // The only sanctioned way out of a PENDING order without paying it.
    @PostMapping("/{paymentIntentId}/cancel")
    public ResponseEntity<Void> cancelCheckout(@PathVariable String paymentIntentId) {
        Integer customerId = currentUserDataUtil.getCurrentUserId();
        checkoutService.cancelCheckout(customerId, paymentIntentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{paymentIntentId}/status")
    public ResponseEntity<CheckoutStatusResponse> getStatus(@PathVariable String paymentIntentId) {
        Integer customerId = currentUserDataUtil.getCurrentUserId();
        return ResponseEntity.ok(checkoutService.getCheckoutStatus(customerId, paymentIntentId));
    }
}
