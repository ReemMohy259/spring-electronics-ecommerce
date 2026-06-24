package com.electronics.util;

import com.electronics.entity.Cart;
import com.electronics.entity.CartItem;
import com.electronics.entity.Customer;
import com.electronics.exception.cart.CartNotFoundException;
import com.electronics.repository.CartItemRepository;
import com.electronics.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CartUtil {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Cart findCartOrThrow(Customer customer) {
        return cartRepository.findByCustomerId(customer.getId())
            .orElseThrow(() -> new CartNotFoundException(customer.getId()));
    }

    @Transactional
    public Cart findOrCreateCart(Customer customer) {
        return cartRepository.findByCustomerId(customer.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCustomer(customer);
            newCart.setTotalQuantity(0);
            newCart.setTotalPrice(BigDecimal.ZERO);
            return cartRepository.save(newCart);
        });
    }

    public void recalculateTotals(Cart cart) {
        List<CartItem> items = cart.getCartItems().stream().toList();

        int totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();

        BigDecimal totalPrice = items.stream()
            .map(
                item -> item.getProduct()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalQuantity(totalQuantity);
        cart.setTotalPrice(totalPrice);
    }
}
