package com.electronics.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSearchRequest {

    private String query;

    private List<String> categories;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Double minRating;

    private ProductSort sort = ProductSort.RELEVANCE;

    private Integer page = 0;

    private Integer size = 20;
}
