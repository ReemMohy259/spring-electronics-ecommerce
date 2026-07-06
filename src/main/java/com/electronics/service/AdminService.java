package com.electronics.service;

import com.electronics.dto.AdminDashboardStatsResponse;
import com.electronics.dto.AdminUserResponse;
import com.electronics.entity.Customer;
import com.electronics.entity.Merchant;
import com.electronics.entity.Order;
import com.electronics.entity.OrderStatus;
import com.electronics.exception.CustomerNotFoundException;
import com.electronics.exception.InvalidRequestException;
import com.electronics.exception.MerchantNotFoundException;
import com.electronics.exception.OrderNotFoundException;
import com.electronics.repository.CustomerRepository;
import com.electronics.repository.MerchantRepository;
import com.electronics.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final OrderRepository orderRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Transactional(readOnly = true)
    public AdminDashboardStatsResponse getDashboardStats() {
        Set<String> adminIds = adminKeycloakIds();
        return new AdminDashboardStatsResponse(
            customerRepository.findAllByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .filter(user -> !adminIds.contains(user.getKeycloakId()))
                .count(),
            merchantRepository.findAllByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .filter(user -> !adminIds.contains(user.getKeycloakId()))
                .count(),
            adminIds.size());
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers(String role) {
        Set<String> adminIds = adminKeycloakIds();
        return switch (role.toUpperCase()) {
            case "CUSTOMER" -> customerRepository.findAllByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .filter(user -> !adminIds.contains(user.getKeycloakId()))
                .map(
                    user -> new AdminUserResponse(
                        user.getId().toString(),
                        user.getEmail(),
                        user.getEmail(),
                        "CUSTOMER",
                        user.getCreatedAt()))
                .toList();
            case "MERCHANT" -> merchantRepository.findAllByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .filter(user -> !adminIds.contains(user.getKeycloakId()))
                .map(
                    user -> new AdminUserResponse(
                        user.getId().toString(),
                        user.getEmail(),
                        user.getBusinessName() == null ? user.getEmail() : user.getBusinessName(),
                        "MERCHANT",
                        user.getCreatedAt()))
                .toList();
            case "ADMIN" -> keycloakAdminService.getUsersWithRealmRole("ADMIN")
                .stream()
                .map(
                    user -> new AdminUserResponse(
                        String.valueOf(user.get("id")),
                        String.valueOf(user.getOrDefault("email", "")),
                        adminName(user),
                        "ADMIN",
                        null))
                .toList();
            default ->
                throw new InvalidRequestException("Unsupported user role", Map.of("role", role));
        };
    }

    private Set<String> adminKeycloakIds() {
        return keycloakAdminService.getUsersWithRealmRole("ADMIN")
            .stream()
            .map(user -> String.valueOf(user.get("id")))
            .collect(Collectors.toSet());
    }

    private String adminName(Map<String, Object> user) {
        String firstName = String.valueOf(user.getOrDefault("firstName", ""));
        String lastName = String.valueOf(user.getOrDefault("lastName", ""));
        String name = (firstName + " " + lastName).trim();
        return name.isEmpty() ? String.valueOf(user.getOrDefault("username", "Administrator"))
            : name;
    }

    @Transactional
    public void softDeleteCustomer(Integer id) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setDeleted(true);
        customerRepository.save(customer);
    }

    @Transactional
    public void softDeleteMerchant(Integer id) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new MerchantNotFoundException(id));
        merchant.setDeleted(true);
        merchantRepository.save(merchant);
    }

    @Transactional
    public void cancelOrder(Integer id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        if (OrderStatus.CANCELLED == order.getStatus()) {
            throw new InvalidRequestException("Order is already cancelled", Map.of("id", id));
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
