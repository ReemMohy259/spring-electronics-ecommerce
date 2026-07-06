package com.electronics.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/stripe")
public class StripeWebhookController {

    private static final String STRIPE_HEADER = "Stripe-Signature";

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
        @RequestBody String payload,
        @RequestHeader(STRIPE_HEADER) String signature) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid payload");
        }

        // switch (event.getType()) {
        // case "payment_intent.succeeded" -> System.out.println("Payment succeeded");
        // case "payment_intent.payment_failed" -> System.out.println("Payment failed");
        // }

        return ResponseEntity.ok("Received");
    }
}
