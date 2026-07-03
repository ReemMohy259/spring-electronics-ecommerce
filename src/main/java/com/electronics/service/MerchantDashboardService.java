package com.electronics.service;

import com.electronics.dto.DashboardStatsResponse;
import com.electronics.dto.PageResponse;
import com.electronics.dto.ProductResponse;
import com.electronics.dto.checkout.OrderResponse;
import com.electronics.entity.Order;
import com.electronics.entity.Product;
import com.electronics.repository.OrderRepository;
import com.electronics.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantDashboardService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboard(String keycloakId) {
        long totalProducts = productRepository.countByMerchantKeycloakIdAndDeletedFalse(keycloakId);
        long totalOrders = orderRepository.countByMerchantKeycloakId(keycloakId);

        Pageable topFive = PageRequest.of(0, 5);
        Page<Product> topProductsPage = productRepository
            .findByMerchantKeycloakIdAndDeletedFalseOrderBySoldUnitsDesc(keycloakId, topFive);
        List<ProductResponse> topProducts = topProductsPage.getContent()
            .stream()
            .map(ProductResponse::from)
            .toList();

        Pageable recentFive = PageRequest.of(0, 5);
        Page<Order> recentOrdersPage = orderRepository
            .findByMerchantKeycloakId(keycloakId, recentFive);
        List<OrderResponse> recentOrders = recentOrdersPage.getContent()
            .stream()
            .map(order -> OrderResponse.fromEntity(order, null))
            .toList();

        BigDecimal totalRevenue = orderRepository.sumRevenueByMerchant(keycloakId);

        return new DashboardStatsResponse(
            totalProducts,
            totalRevenue,
            totalOrders,
            topProducts,
            recentOrders);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(String keycloakId, Pageable pageable) {
        Page<Product> products = productRepository
            .findByMerchantKeycloakIdAndDeletedFalse(keycloakId, pageable);
        return PageResponse.from(products, ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getOrders(String keycloakId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByMerchantKeycloakId(keycloakId, pageable);
        return PageResponse.from(orders, order -> OrderResponse.fromEntity(order, null));
    }
}
