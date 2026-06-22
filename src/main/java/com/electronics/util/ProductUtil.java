package com.electronics.util;

import com.electronics.entity.Product;
import com.electronics.exception.InsufficientStockException;
import com.electronics.exception.ProductNotFoundException;
import com.electronics.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductUtil {

    private final ProductRepository productRepository;

    public Product findProductOrThrow(Integer productId) {
        return productRepository.findByIdAndDeletedFalse(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public void validateStock(Product product, int requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            throw new InsufficientStockException(product.getId(),
                requestedQuantity, product.getStockQuantity());
        }
    }
}
