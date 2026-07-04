package com.electronics.service;

import com.electronics.dto.checkout.OrderItemResponse;
import com.electronics.dto.checkout.OrderResponse;
import com.electronics.entity.*;
import com.electronics.repository.OrderRepository;
import com.electronics.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
        OrderStatus.PENDING,
        Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
        OrderStatus.PAID,
        Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
        OrderStatus.SHIPPED,
        Set.of(OrderStatus.DELIVERED),
        OrderStatus.DELIVERED,
        Set.of(),
        OrderStatus.CANCELLED,
        Set.of());

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Pageable pageable) {
        Customer customer = getCurrentCustomer();
        return orderRepository.findByCustomer(customer, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrderById(Integer id) {
        Customer customer = getCurrentCustomer();
        Order order = orderRepository.findByIdAndCustomer(id, customer)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdAdmin(Integer id) {
        Order order = findOrderOrThrow(id);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Integer id, OrderStatus newStatus) {
        Order order = findOrderOrThrow(id);
        OrderStatus current = order.getStatus();

        if (current == newStatus) {
            return toResponse(order);
        }

        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new IllegalStateException(
                "Cannot transition order from %s to %s".formatted(current, newStatus));
        }

        order.setStatus(newStatus);
        return toResponse(orderRepository.save(order));
    }

    private Order findOrderOrThrow(Integer id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    private Customer getCurrentCustomer() {
        User user = currentUserService.getCurrentUserEntity();
        if (!(user instanceof Customer customer)) {
            throw new RuntimeException("Only customers can access this resource");
        }
        return customer;
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems()
            .stream()
            .map(this::toItemResponse)
            .toList();

        String paymentIntentId = paymentRepository.findByOrder(order)
            .map(Payment::getStripePaymentIntentId)
            .orElse(null);

        return new OrderResponse(
            order.getId(),
            order.getStatus(),
            order.getTotalPrice(),
            order.getTimestamp(),
            paymentIntentId,
            items);
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        BigDecimal subtotal = item.getCurrentPrice()
            .multiply(BigDecimal.valueOf(item.getQuantity()));
        return new OrderItemResponse(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getProduct().getImageUrl(),
            item.getQuantity(),
            item.getCurrentPrice(),
            subtotal);
    }
}
