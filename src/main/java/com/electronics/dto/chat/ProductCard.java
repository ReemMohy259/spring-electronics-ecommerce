package com.electronics.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class ProductCard {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String sku;
    private Set<String> categories; // Derived from product.getCategories()
    private String merchantName; // Derived from product.getMerchant()
    private boolean inStock; // Derived: stockQuantity > 0
    private Integer stockQuantity;
    private String actionUrl;
    private String additionalInfo;
}
