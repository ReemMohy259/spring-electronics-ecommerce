package com.electronics.service;

import com.electronics.dto.checkout.CheckoutInitResponse;
import com.electronics.dto.checkout.ConfirmCheckoutRequest;
import com.electronics.dto.checkout.OrderResponse;
import com.electronics.entity.*;
import com.electronics.exception.cart.CartNotFoundException;
import com.electronics.exception.CustomerNotFoundException;
import com.electronics.exception.cart.EmptyCartException;
import com.electronics.exception.payment.PaymentFailedException;
import com.electronics.exception.payment.PaymentNotFoundException;
import com.electronics.exception.product.InsufficientStockException;
import com.electronics.exception.product.ProductNotFoundException;
import com.electronics.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private static final String CURRENCY = "egp";

    private final StripePaymentService stripePaymentService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public CheckoutInitResponse initiateCheckout(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));

        // 1. Load cart
        Cart cart = cartRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new CartNotFoundException(customerId));

        // 2. Validate cart is not empty
        if (cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Cannot checkout with an empty cart");
        }

        // 3. Validate stock for every item in the cart
        validateCart(cart);

        // 4. Create Stripe PaymentIntent
        PaymentIntent intent;
        try {
            intent = stripePaymentService.createPaymentIntent(cart.getTotalPrice(), CURRENCY);
        } catch (StripeException e) {
            throw new PaymentFailedException("Failed to initiate payment: " + e.getMessage());
        }

        // 5. Persist a PENDING Payment record
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setStripePaymentIntentId(intent.getId());
        payment.setAmount(cart.getTotalPrice());
        payment.setCurrency(CURRENCY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setUpdatedAt(OffsetDateTime.now());

        paymentRepository.save(payment);

        return new CheckoutInitResponse(
            intent.getClientSecret(),
            intent.getId(),
            CURRENCY,
            cart.getTotalPrice());
    }

    public OrderResponse confirmCheckout(Integer customerId, ConfirmCheckoutRequest request) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));

        // 1. Load the Payment record
        Payment payment = paymentRepository.findByStripePaymentIntentId(request.paymentIntentId())
            .orElseThrow(
                () -> new PaymentNotFoundException(
                    "Payment not found for intent: " + request.paymentIntentId()));

        // 2. Idempotency guard — don't process twice
        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            Order existingOrder = payment.getOrder();
            return OrderResponse.fromEntity(existingOrder, payment.getStripePaymentIntentId());
        }

        // 3. Verify status with Stripe (never trust the frontend)
        PaymentIntent intent;
        try {
            intent = stripePaymentService.retrievePaymentIntent(request.paymentIntentId());
        } catch (StripeException e) {
            throw new PaymentFailedException(
                "Failed to verify payment with Stripe: " + e.getMessage());
        }

        // 4. Check Stripe says it actually succeeded
        if (!"succeeded".equals(intent.getStatus())) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setUpdatedAt(OffsetDateTime.now());
            paymentRepository.save(payment);
            throw new PaymentFailedException(
                "Payment was not successful. Stripe status: " + intent.getStatus());
        }

        // 5. Re-load the cart and re-validate stock (race condition protection)
        Cart cart = cartRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new CartNotFoundException(customerId));

        validateCart(cart);

        // 6. Create Order
        Order order = new Order();
        order.setUser(customer);
        order.setTotalPrice(payment.getAmount());
        order.setStatus("CONFIRMED");
        order.setTimestamp(OffsetDateTime.now());
        orderRepository.save(order);

        // 7. Create OrderItems + deduct stock
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setCurrentPrice(product.getPrice()); // price snapshot at purchase time
            orderItemRepository.save(orderItem);

            order.getOrderItems().add(orderItem);

            // Deduct stock and increment sold units
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            product.setSoldUnits(product.getSoldUnits() + item.getQuantity());
            productRepository.save(product);
        }

        // 8. Link Payment to Order and mark SUCCEEDED
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setUpdatedAt(OffsetDateTime.now());
        paymentRepository.save(payment);

        // 9. Clear the cart
        cartService.clearCart(cart);

        return OrderResponse.fromEntity(order, payment.getStripePaymentIntentId());
    }

    private void validateCart(Cart cart) {
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();

            if (product.getDeleted()) {
                throw new ProductNotFoundException(product.getId());
            }

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(
                    product.getId(),
                    item.getQuantity(),
                    product.getStockQuantity());
            }
        }
    }
}
