package com.electronics.controller;

import com.electronics.service.CheckoutService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private static final String STRIPE_HEADER = "Stripe-Signature";

    private final CheckoutService checkoutService;

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
            log.warn("Stripe webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            log.warn("Stripe webhook payload could not be parsed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid payload");
        }

        try {
            switch (event.getType()) {
                case "payment_intent.succeeded" -> {
                    PaymentIntent intent = extractPaymentIntent(event);
                    checkoutService.finalizeSuccessfulPayment(intent.getId());
                }
                case "payment_intent.payment_failed" -> {
                    PaymentIntent intent = extractPaymentIntent(event);
                    checkoutService.markPaymentFailed(intent.getId());
                }
                default -> log.debug("Ignoring unhandled Stripe event type: {}", event.getType());
            }
        } catch (Exception e) {
            // Returning 5xx tells Stripe to retry delivery. That matters here: a
            // transient failure (DB hiccup, lock timeout, etc.) must not be treated
            // as "this event was handled" — CheckoutService's own idempotency check
            // (payment.status == SUCCEEDED) makes retries safe.
            log.error(
                "Failed to process Stripe webhook event {} ({}): {}",
                event.getId(),
                event.getType(),
                e.getMessage(),
                e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing error");
        }

        return ResponseEntity.ok("Received");
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        return (PaymentIntent) deserializer.getObject()
            .orElseThrow(
                () -> new IllegalStateException(
                    "Could not deserialize PaymentIntent for event " + event.getId()));
    }
}
