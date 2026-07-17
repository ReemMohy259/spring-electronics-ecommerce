package com.electronics.dto;

import com.electronics.dto.checkout.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsResponse(long totalProducts, BigDecimal totalRevenue, long totalOrders,
    List<ProductResponse> topProducts, List<OrderResponse> recentOrders) {
}
