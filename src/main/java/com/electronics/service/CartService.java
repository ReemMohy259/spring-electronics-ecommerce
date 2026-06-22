package com.electronics.service;

import com.electronics.dto.cart.AddToCartRequest;
import com.electronics.dto.cart.CartResponse;
import com.electronics.entity.Cart;
import com.electronics.entity.CartItem;
import com.electronics.entity.Customer;
import com.electronics.entity.Product;
import com.electronics.exception.CustomerNotFoundException;
import com.electronics.exception.InsufficientStockException;
import com.electronics.exception.ProductNotFoundException;
import com.electronics.repository.CartItemRepository;
import com.electronics.repository.CartRepository;
import com.electronics.repository.CustomerRepository;
import com.electronics.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public CartResponse addToCart(Integer customerId, AddToCartRequest addToCartRequest) {
        Product product = productRepository.findById(addToCartRequest.productId())
            .orElseThrow(() -> new ProductNotFoundException(addToCartRequest.productId()));

        if (product.getStockQuantity() < addToCartRequest.quantity()) {
            throw new InsufficientStockException(addToCartRequest.productId(),
                addToCartRequest.quantity(), product.getStockQuantity());
        }

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));

        Cart cart = cartRepository.findByCustomerId(customerId)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setCustomer(customer);
                newCart.setTotalPrice(BigDecimal.ZERO);
                newCart.setTotalQuantity(0);
                return newCart;
            });

        cartRepository.save(cart);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), addToCartRequest.productId())
            .orElseGet(() -> {
                CartItem newItem = new CartItem();
                newItem.setProduct(product);
                newItem.setQuantity(0);
                newItem.setCart(cart);
                return newItem;
            });

        item.setQuantity(item.getQuantity() + addToCartRequest.quantity());

        cart.setTotalQuantity(cart.getTotalQuantity() + addToCartRequest.quantity());
        cart.setTotalPrice(cart.getTotalPrice().add(
            item.getProduct().getPrice().multiply(BigDecimal.valueOf(addToCartRequest.quantity()))));

        return null;
    }
}
