package com.electronics.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_gen")
    @SequenceGenerator(name = "product_id_gen", sequenceName = "product_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sold_units", nullable = false)
    private Integer soldUnits;

    @Size(max = 100)
    @Column(name = "sku", length = 100, unique = true)
    private String sku;

    @Column(name = "image_url", length = Integer.MAX_VALUE)
    private String imageUrl;

    @Column(name = "additional_info", length = Integer.MAX_VALUE)
    private String additionalInfo;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @ManyToMany
    @BatchSize(size = 50)
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"), uniqueConstraints = @UniqueConstraint(name = "uk_product_category", columnNames = {
            "product_id", "category_id"}))
    private Set<Category> categories = new LinkedHashSet<>();

    @PrePersist
    void initializeDefaults() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
        if (soldUnits == null) {
            soldUnits = 0;
        }
        if (deleted == null) {
            deleted = false;
        }
    }
}
