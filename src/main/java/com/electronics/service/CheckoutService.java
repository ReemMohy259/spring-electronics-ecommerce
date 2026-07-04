package com.electronics.service;

import com.electronics.dto.checkout.CheckoutInitResponse;
import com.electronics.dto.checkout.ConfirmCheckoutRequest;
import com.electronics.dto.checkout.OrderResponse;
import com.electronics.entity.*;
import com.electronics.exception.cart.CartNotFoundException;
import com.electronics.exception.CustomerNotFoundException;
import com.electronics.exception.cart.EmptyCartException;
import com.electronics.exception.payment.CartTotalMismatchException;
import com.electronics.exception.payment.PaymentFailedException;
import com.electronics.exception.payment.PaymentNotFoundException;
import com.electronics.exception.product.InsufficientStockException;
import com.electronics.exception.product.ProductNotFoundException;
import com.electronics.repository.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: 1. Concurrent double-/init — two rapid clicks racing before the first transaction commits,
//          both finding no pending payment, both creating intents
// TODO: 2. Abandoned-checkout cleanup job

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutService {

    private static final String CURRENCY = "egp";

    // Stripe PaymentIntent statuses that can still be confirmed against without
    // creating a new intent. "succeeded" and "canceled" are deliberately excluded
    // and handled as their own branches below — retrieval succeeding tells you
    // nothing about whether the intent is still usable.
    private static final Set<String> REUSABLE_INTENT_STATUSES = Set
        .of("requires_payment_method", "requires_confirmation", "requires_action");

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

        // 4. Check your existing PENDING payment
        Optional<Payment> existingPayment = paymentRepository
            .findByCustomerIdAndStatus(customerId, PaymentStatus.PENDING);

        // 5. Handle existing payment if found
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            boolean amountUnchanged = payment.getAmount().compareTo(cart.getTotalPrice()) == 0;

            PaymentIntent intent;
            try {
                intent = stripePaymentService
                    .retrievePaymentIntent(payment.getStripePaymentIntentId());
            } catch (StripeException e) {
                // Retrieval itself failed — can't trust this row, retire it and fall through.
                cancelExistingPayment(payment);
                intent = null;
            }

            if (intent != null) {
                // Retrieval succeeding tells us nothing about reusability on its own —
                // Stripe happily returns already-succeeded or already-canceled intents
                // without error. Status is the only thing that actually tells us.
                if (amountUnchanged && REUSABLE_INTENT_STATUSES.contains(intent.getStatus())) {
                    return new CheckoutInitResponse(
                        intent.getClientSecret(),
                        intent.getId(),
                        CURRENCY,
                        cart.getTotalPrice());
                }

                if ("succeeded".equals(intent.getStatus())) {
                    // Already paid but our row is still PENDING (webhook lag, or the
                    // confirm call is in flight elsewhere). Don't hand out a dead
                    // clientSecret for an intent Stripe will refuse to reconfirm —
                    // sync our record and tell the caller to confirm, not re-init.
                    payment.setStatus(PaymentStatus.SUCCEEDED);
                    payment.setUpdatedAt(OffsetDateTime.now());
                    paymentRepository.save(payment);
                    throw new PaymentFailedException(
                        "This checkout was already paid. Please confirm instead of restarting.");
                }

                // Amount changed, or Stripe no longer considers it reusable
                // (expired/canceled/processing/requires_capture) — retire it.
                cancelExistingPayment(payment);
            }
        }

        // 6. Create Stripe PaymentIntent
        PaymentIntent intent;
        try {
            intent = stripePaymentService.createPaymentIntent(cart.getTotalPrice(), CURRENCY);
        } catch (StripeException e) {
            throw new PaymentFailedException("Failed to initiate payment: " + e.getMessage());
        }

        // 7. Persist a PENDING Payment record
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

    @Transactional
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

        // 5. Re-load the cart
        Cart cart = cartRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new CartNotFoundException(customerId));

        // 6. Verify the cart total matches what was charged
        if (payment.getAmount().compareTo(cart.getTotalPrice()) != 0) {
            log.warn(
                "Amount mismatch for customer {}: Charged {}, Cart total {}",
                customerId,
                payment.getAmount(),
                cart.getTotalPrice());

            throw new CartTotalMismatchException(payment.getAmount(), cart.getTotalPrice());
        }

        // 7. Lock and validate products with pessimistic locking
        // This prevents race conditions where two concurrent checkouts try to buy the
        // last item
        List<Product> lockedProducts = lockAndValidateProducts(cart);

        // 8. Create Order
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalPrice(payment.getAmount());
        order.setStatus(OrderStatus.PAID);
        order.setTimestamp(OffsetDateTime.now());
        orderRepository.save(order);

        // 9. Create OrderItems + deduct stock (using locked products)
        for (CartItem item : cart.getCartItems()) {
            // Use the locked product from our earlier query
            Product product = lockedProducts.stream()
                .filter(p -> p.getId().equals(item.getProduct().getId()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(item.getProduct().getId()));

            // Double-check stock one more time (safety check)
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(
                    product.getId(),
                    item.getQuantity(),
                    product.getStockQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setCurrentPrice(product.getPrice());
            orderItemRepository.save(orderItem);

            order.getOrderItems().add(orderItem);

            // Deduct stock and increment sold units
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            product.setSoldUnits(product.getSoldUnits() + item.getQuantity());
            // productRepository.save(product) is not needed here because product is managed
            // and will be saved when the transaction commits
        }

        // 10. Link Payment to Order and mark SUCCEEDED
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setUpdatedAt(OffsetDateTime.now());
        paymentRepository.save(payment);

        // 11. Clear the cart
        cartService.clearCart(cart);

        return OrderResponse.fromEntity(order, payment.getStripePaymentIntentId());
    }

    private List<Product> lockAndValidateProducts(Cart cart) {
        // Collect all product IDs from the cart, sorted so that any two concurrent
        // checkouts touching overlapping products always acquire locks in the same
        // order — this is what actually prevents a deadlock, not just relying on
        // however the DB happens to plan the IN-clause scan.
        List<Integer> ids = cart.getCartItems()
            .stream()
            .map(item -> item.getProduct().getId())
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        // Load and lock all products in a single query
        List<Product> lockedProducts = productRepository.findAllByIdForUpdate(ids);

        // Verify we got all products
        if (lockedProducts.size() != ids.size()) {
            List<Integer> foundIds = lockedProducts.stream().map(Product::getId).toList();
            ids.removeAll(foundIds);
            throw new ProductNotFoundException(ids);
        }

        // Validate stock for each product
        for (CartItem item : cart.getCartItems()) {
            Product product = lockedProducts.stream()
                .filter(p -> p.getId().equals(item.getProduct().getId()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(item.getProduct().getId()));

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

        return lockedProducts;
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

    private void cancelExistingPayment(Payment payment) {
        try {
            stripePaymentService.cancelPaymentIntent(payment.getStripePaymentIntentId());
        } catch (StripeException e) {
            log.warn(
                "Failed to cancel stale PaymentIntent {} in Stripe: {}",
                payment.getStripePaymentIntentId(),
                e.getMessage());
            // Don't fail the request over this — we're creating a new intent regardless,
            // and a leftover unconfirmed intent in Stripe expires on its own after 24h.
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setUpdatedAt(OffsetDateTime.now());
        paymentRepository.save(payment);
    }
}
