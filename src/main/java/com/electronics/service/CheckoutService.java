package com.electronics.service;

import com.electronics.dto.checkout.CheckoutInitResponse;
import com.electronics.dto.checkout.CheckoutStatusResponse;
import com.electronics.dto.checkout.OrderResponse;
import com.electronics.entity.*;
import com.electronics.exception.cart.CartNotFoundException;
import com.electronics.exception.CustomerNotFoundException;
import com.electronics.exception.cart.EmptyCartException;
import com.electronics.exception.checkout.ActiveCheckoutExistsException;
import com.electronics.exception.payment.PaymentAlreadyProcessedException;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: 1. Concurrent double-init — two rapid clicks racing before the first transaction
//          commits, both finding no pending payment, both creating intents.
// TODO: 2. Abandoned-checkout cleanup job — PENDING payments/orders whose PaymentIntent
//          never completes (Stripe cancels the intent after 24h; our rows need the same).
// TODO: 3. Stock-at-fulfillment failure — if finalizeSuccessfulPayment() finds
//          insufficient stock for an already-*paid* PaymentIntent, money has moved but
//          we can't fulfill. Currently this cancels the order and logs at ERROR for
//          manual refund; wiring an automatic Stripe refund is a follow-up.

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
        Cart cart = cartRepository.findByCustomerIdWithItems(customerId)
            .orElseThrow(() -> new CartNotFoundException(customerId));

        // 2. Validate cart is not empty
        if (cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Cannot checkout with an empty cart");
        }

        BigDecimal cartTotal = cart.getCartItems()
            .stream()
            .map(
                item -> item.getProduct()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Validate stock for every item in the cart
        validateCart(cart);

        // 4. Check your existing PENDING payment
        Optional<Payment> existingPayment = paymentRepository
            .findByCustomerIdAndStatus(customerId, PaymentStatus.PENDING);

        // 5. Handle existing payment if found
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();

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
                if (REUSABLE_INTENT_STATUSES.contains(intent.getStatus())) {
                    throw new ActiveCheckoutExistsException(
                        new CheckoutInitResponse(
                            intent.getClientSecret(),
                            intent.getId(),
                            CURRENCY,
                            payment.getAmount(),
                            OrderResponse.fromEntity(
                                payment.getOrder(),
                                payment.getStripePaymentIntentId())));
                }

                if ("succeeded".equals(intent.getStatus())) {
                    // Webhook/reconciliation may just not have landed yet. This is
                    // a conflict (already resolved), not a payment failure.
                    throw new PaymentAlreadyProcessedException(
                        intent.getId(),
                        PaymentStatus.SUCCEEDED);
                }

                // Stripe itself has already retired this intent (expired,
                // canceled some other way) — that's cleanup, not the customer
                // creating a second order. Safe to retire our row and proceed.
                cancelExistingPayment(payment);
            }
        }

        // Snapshot the cart into an Order *now*, at the same moment we price the
        // PaymentIntent. finalizeSuccessfulPayment() never re-reads the live cart —
        // it works off this snapshot — so a cart edited elsewhere after this point
        // can never change what an in-flight payment pays for.
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalPrice(cartTotal);
        order.setStatus(OrderStatus.PENDING);
        order.setTimestamp(OffsetDateTime.now());
        orderRepository.save(order);

        for (CartItem item : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setCurrentPrice(item.getProduct().getPrice());
            orderItemRepository.save(orderItem);
            order.getOrderItems().add(orderItem);
        }

        // 6. Create Stripe PaymentIntent
        PaymentIntent intent;
        try {
            intent = stripePaymentService.createPaymentIntent(cartTotal, CURRENCY);
        } catch (StripeException e) {
            throw new PaymentFailedException(null, e.getMessage());
        }

        // 7. Persist a PENDING Payment record
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setOrder(order);
        payment.setStripePaymentIntentId(intent.getId());
        payment.setAmount(cartTotal);
        payment.setCurrency(CURRENCY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setUpdatedAt(OffsetDateTime.now());
        paymentRepository.save(payment);

        return new CheckoutInitResponse(
            intent.getClientSecret(),
            intent.getId(),
            CURRENCY,
            cartTotal,
            OrderResponse.fromEntity(order, intent.getId()));
    }

    @Transactional
    public void cancelCheckout(Integer customerId, String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentIntentId));

        if (!payment.getCustomer().getId().equals(customerId)) {
            throw new PaymentNotFoundException(paymentIntentId);
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            // Already paid, failed, or cancelled — nothing left to cancel.
            throw new PaymentAlreadyProcessedException(paymentIntentId, payment.getStatus());
        }

        cancelExistingPayment(payment);
    }

    @Transactional
    public void finalizeSuccessfulPayment(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
            .orElseThrow(
                () -> new PaymentNotFoundException(
                    "Payment not found for intent: " + paymentIntentId));

        // Idempotency guard — Stripe delivers webhooks at-least-once, so retries and
        // duplicate deliveries are expected, not exceptional.
        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            return;
        }

        Order order = payment.getOrder();

        List<Product> lockedProducts;
        try {
            lockedProducts = lockAndValidateProducts(order.getOrderItems());
        } catch (InsufficientStockException | ProductNotFoundException e) {
            // Money has already moved on Stripe's side, but we can't fulfill. We
            // don't have an automatic refund wired up yet (see TODO), so this needs
            // a human. Mark it clearly rather than rethrowing — rethrowing would
            // make us return 5xx and Stripe would keep re-delivering this event
            // forever, since the stock problem won't fix itself on retry.
            log.error(
                "Payment {} succeeded but order {} cannot be fulfilled: {}. Needs manual refund.",
                paymentIntentId,
                order.getId(),
                e.getMessage());
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            payment.setStatus(PaymentStatus.SUCCEEDED);
            payment.setUpdatedAt(OffsetDateTime.now());
            paymentRepository.save(payment);
            return;
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = lockedProducts.stream()
                .filter(p -> p.getId().equals(orderItem.getProduct().getId()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(orderItem.getProduct().getId()));

            product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());
            product.setSoldUnits(product.getSoldUnits() + orderItem.getQuantity());
            // managed entity — persisted on commit, same as before
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setUpdatedAt(OffsetDateTime.now());
        paymentRepository.save(payment);

        cartRepository.findByCustomerId(payment.getCustomer().getId())
            .ifPresent(cart -> cartService.removeItemsForOrder(cart, order));
    }

    /** Called from the webhook on payment_intent.payment_failed. */
    @Transactional
    public void markPaymentFailed(String paymentIntentId) {
        Optional<Payment> maybePayment = paymentRepository
            .findByStripePaymentIntentId(paymentIntentId);
        if (maybePayment.isEmpty()) {
            return; // nothing to do
        }

        Payment payment = maybePayment.get();
        if (payment.getStatus() != PaymentStatus.PENDING) {
            return; // already finalized one way or another — ignore
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setUpdatedAt(OffsetDateTime.now());
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        if (order != null) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
        // Cart is left untouched — the customer's items are still there to retry.
    }

    /** Polled by the frontend after stripe.confirmPayment() resolves. */
    @Transactional(readOnly = true)
    public CheckoutStatusResponse getCheckoutStatus(Integer customerId, String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
            .orElseThrow(
                () -> new PaymentNotFoundException(
                    "Payment not found for intent: " + paymentIntentId));

        if (!payment.getCustomer().getId().equals(customerId)) {
            // Don't confirm or deny existence of another customer's payment.
            throw new PaymentNotFoundException("Payment not found for intent: " + paymentIntentId);
        }

        return switch (payment.getStatus()) {
            case SUCCEEDED -> CheckoutStatusResponse
                .paid(OrderResponse.fromEntity(payment.getOrder(), paymentIntentId));
            case FAILED, CANCELLED -> CheckoutStatusResponse.failed();
            case PENDING -> CheckoutStatusResponse.pending();
        };
    }

    private List<Product> lockAndValidateProducts(Set<OrderItem> orderItems) {
        // Sorted so any two concurrent operations touching overlapping products
        // always acquire locks in the same order — prevents deadlock.
        List<Integer> ids = orderItems.stream()
            .map(item -> item.getProduct().getId())
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        List<Product> lockedProducts = productRepository.findAllByIdForUpdate(ids);

        if (lockedProducts.size() != ids.size()) {
            List<Integer> foundIds = lockedProducts.stream().map(Product::getId).toList();
            ids.removeAll(foundIds);
            throw new ProductNotFoundException(ids);
        }

        for (OrderItem item : orderItems) {
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
