package com.electronics.service;

import com.electronics.dto.cart.AddToCartRequest;
import com.electronics.dto.cart.CartResponse;
import com.electronics.dto.cart.UpdateCartItemRequest;
import com.electronics.entity.*;
import com.electronics.exception.cart.CartItemNotFoundException;
import com.electronics.repository.CartItemRepository;
import com.electronics.repository.CartRepository;
import com.electronics.repository.CustomerRepository;
import com.electronics.repository.ProductRepository;
import com.electronics.util.CartUtil;
import com.electronics.util.CustomerUtil;
import com.electronics.util.ProductUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CartUtil cartUtil;
    private final ProductUtil productUtil;
    private final CustomerUtil customerUtil;

    public CartService(
        CartRepository cartRepository,
        CartItemRepository cartItemRepository,
        ProductRepository productRepository,
        CustomerRepository customerRepository,
        @Lazy CartUtil cartUtil,
        @Lazy ProductUtil productUtil,
        @Lazy CustomerUtil customerUtil) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.cartUtil = cartUtil;
        this.productUtil = productUtil;
        this.customerUtil = customerUtil;
    }

    @Transactional
    public CartResponse getCart(Integer customerId) {
        Customer customer = customerUtil.findCustomerOrThrow(customerId);
        Cart cart = cartUtil.findOrCreateCart(customer);
        return CartResponse.fromEntity(cart);
    }

    @Transactional
    public CartResponse addToCart(Integer customerId, AddToCartRequest request) {
        Product product = productUtil.findProductOrThrow(request.productId());

        productUtil.validateStock(product, request.quantity());

        Customer customer = customerUtil.findCustomerOrThrow(customerId);

        Cart cart = cartUtil.findOrCreateCart(customer);

        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
            .ifPresentOrElse(existingItem -> {
                int newQuantity = existingItem.getQuantity() + request.quantity();
                productUtil.validateStock(product, newQuantity);
                existingItem.setQuantity(newQuantity);
                cartItemRepository.save(existingItem);
            }, () -> {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(request.quantity());
                cartItemRepository.save(newItem);
                cart.getCartItems().add(newItem);
            });

        cartUtil.recalculateTotals(cart);
        cartRepository.save(cart);

        return CartResponse.fromEntity(cart);
    }

    @Transactional
    public CartResponse updateItem(
        Integer customerId,
        Integer productId,
        UpdateCartItemRequest request) {
        if (request.quantity() == 0) {
            return removeItem(customerId, productId);
        }

        Product product = productUtil.findProductOrThrow(productId);
        Customer customer = customerUtil.findCustomerOrThrow(customerId);

        Cart cart = cartUtil.findCartOrThrow(customer);

        CartItem cartItem = cartItemRepository
            .findByCartIdAndProductId(cart.getId(), product.getId())
            .orElseThrow(() -> new CartItemNotFoundException(productId));

        productUtil.validateStock(product, request.quantity());

        cartItem.setQuantity(request.quantity());
        cartItemRepository.save(cartItem);

        cartUtil.recalculateTotals(cart);

        cartRepository.save(cart);

        return CartResponse.fromEntity(cart);
    }

    @Transactional
    public CartResponse removeItem(Integer customerId, Integer productId) {
        Product product = productUtil.findProductOrThrow(productId);
        Customer customer = customerUtil.findCustomerOrThrow(customerId);

        Cart cart = cartUtil.findCartOrThrow(customer);

        CartItem cartItem = cartItemRepository
            .findByCartIdAndProductId(cart.getId(), product.getId())
            .orElseThrow(() -> new CartItemNotFoundException(productId));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        cartUtil.recalculateTotals(cart);

        cartRepository.save(cart);

        return CartResponse.fromEntity(cart);
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cartItemRepository.deleteByCart(cart);
        cart.setTotalQuantity(0);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    public void removeItemsForOrder(Cart cart, Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            cart.getCartItems()
                .stream()
                .filter(ci -> ci.getProduct().getId().equals(orderItem.getProduct().getId()))
                .findFirst()
                .ifPresent(cartItem -> {
                    if (cartItem.getQuantity() <= orderItem.getQuantity()) {
                        // Fully covered by what was paid for — drop the line entirely.
                        cart.removeItem(cartItem);
                        cartItemRepository.delete(cartItem); // adjust to your actual repo field
                    } else {
                        // Customer added more of this product after checkout started —
                        // only remove the quantity that was actually paid for.
                        cartItem.setQuantity(cartItem.getQuantity() - orderItem.getQuantity());
                    }
                });
            // If no matching cartItem was found, the customer already removed it
            // themselves before the webhook landed — nothing to reconcile.
        }

        // Recompute totalQuantity / totalPrice however your class currently does it
        // (e.g. a private recalculateTotals(cart) helper), then persist.
        cartRepository.save(cart);
    }
}
