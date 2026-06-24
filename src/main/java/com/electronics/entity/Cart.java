package com.electronics.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_id_gen")
    @SequenceGenerator(name = "cart_id_gen", sequenceName = "cart_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "cart")
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    public void addItem(CartItem cartItem) {
        cartItem.setCart(this);
        cartItems.add(cartItem);
    }

    public void removeItem(CartItem cartItem) {
        cartItem.setCart(null);
        cartItems.remove(cartItem);
    }

    public void updateItem(Integer productId, int quantity) {
        CartItem item = cartItems.stream()
            .filter(i -> i.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow();

        item.setQuantity(quantity);
    }
}