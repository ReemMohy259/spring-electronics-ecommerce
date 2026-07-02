package com.electronics.service;

import com.electronics.dto.CurrentUser;
import com.electronics.dto.PageResponse;
import com.electronics.dto.ProductRequest;
import com.electronics.dto.ProductResponse;
import com.electronics.entity.Category;
import com.electronics.entity.Merchant;
import com.electronics.entity.Product;
import com.electronics.exception.DuplicateResourceException;
import com.electronics.exception.InvalidRequestException;
import com.electronics.exception.MerchantNotFoundException;
import com.electronics.exception.product.ProductNotFoundException;
import com.electronics.repository.CategoryRepository;
import com.electronics.repository.MerchantRepository;
import com.electronics.repository.ProductRepository;
import com.electronics.repository.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set
        .of("id", "name", "price", "stockQuantity", "soldUnits", "createdAt");

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MerchantRepository merchantRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAll(
        Integer categoryId,
        BigDecimal minimumPrice,
        BigDecimal maximumPrice,
        String keyword,
        Pageable pageable) {
        validatePriceRange(minimumPrice, maximumPrice);
        Specification<Product> specification = ProductSpecifications.isActive();
        if (categoryId != null) {
            specification = specification.and(ProductSpecifications.hasCategory(categoryId));
        }
        if (minimumPrice != null) {
            specification = specification.and(ProductSpecifications.priceAtLeast(minimumPrice));
        }
        if (maximumPrice != null) {
            specification = specification.and(ProductSpecifications.priceAtMost(maximumPrice));
        }
        if (StringUtils.hasText(keyword)) {
            specification = specification.and(ProductSpecifications.containsKeyword(keyword));
        }

        Page<Product> products = productRepository
            .findAll(specification, normalizePageable(pageable));
        return PageResponse.from(products, ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findFeatured(
        Integer categoryId,
        BigDecimal minimumPrice,
        BigDecimal maximumPrice,
        String keyword,
        Pageable pageable) {
        validatePriceRange(minimumPrice, maximumPrice);
        String searchKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        Pageable featuredPageable = PageRequest.of(pageable.getPageNumber(), size, Sort.unsorted());
        Page<Product> products = productRepository
            .findFeatured(categoryId, minimumPrice, maximumPrice, searchKeyword, featuredPageable);
        return PageResponse.from(products, ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Integer id) {
        return ProductResponse.from(getActiveProduct(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        Merchant merchant = resolveMerchant(request.merchantId());
        applyRequest(product, request, null, merchant);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Integer id, ProductRequest request) {
        boolean admin = isAdmin();
        Product product = getManageableProduct(id);
        Merchant merchant = admin ? getMerchant(request.merchantId()) : product.getMerchant();
        if (!admin && !merchant.getId().equals(request.merchantId())) {
            throw new InvalidRequestException(
                "Merchants cannot transfer products to another merchant");
        }
        applyRequest(product, request, id, merchant);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void softDelete(Integer id) {
        Product product = getManageableProduct(id);
        product.setDeleted(true);
        productRepository.save(product);
    }

    private Product getActiveProduct(Integer id) {
        return productRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void applyRequest(
        Product product,
        ProductRequest request,
        Integer currentProductId,
        Merchant merchant) {
        String sku = normalizeOptional(request.sku());
        ensureSkuAvailable(sku, currentProductId);

        Set<Category> categories = loadCategories(request.categoryIds());

        product.setMerchant(merchant);
        product.setName(request.name().trim());
        product.setDescription(normalizeOptional(request.description()));
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setSku(sku);
        product.setImageUrl(normalizeOptional(request.imageUrl()));
        product.setAdditionalInfo(normalizeOptional(request.additionalInfo()));
        product.setCategories(categories);
    }

    private Product getManageableProduct(Integer id) {
        if (isAdmin()) {
            return getActiveProduct(id);
        }
        return productRepository
            .findByIdAndDeletedFalseAndMerchantKeycloakId(
                id,
                currentUserService.getCurrentUser().keycloakId())
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private Merchant resolveMerchant(Integer merchantId) {
        if (isAdmin()) {
            return getMerchant(merchantId);
        }
        CurrentUser c = currentUserService.getCurrentUser();
        Merchant merchant = merchantRepository.findByEmail(c.email())
            .orElseThrow(() -> new MerchantNotFoundException(merchantId));
        if (!merchant.getId().equals(merchantId)) {
            throw new InvalidRequestException(
                "Merchants can only create products under their own account");
        }
        return merchant;
    }

    private Merchant getMerchant(Integer merchantId) {
        return merchantRepository.findById(merchantId)
            .orElseThrow(() -> new MerchantNotFoundException(merchantId));
    }

    private boolean isAdmin() {
        return getAuthentication().getAuthorities()
            .stream()
            .anyMatch(authority -> Objects.equals(authority.getAuthority(), "ROLE_ADMIN"));
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Set<Category> loadCategories(Set<Integer> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        Set<Integer> foundIds = categories.stream()
            .map(Category::getId)
            .collect(Collectors.toSet());
        Set<Integer> missingIds = categoryIds.stream()
            .filter(id -> !foundIds.contains(id))
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!missingIds.isEmpty()) {
            throw new InvalidRequestException(
                "One or more categories do not exist",
                Map.of("missingCategoryIds", missingIds));
        }
        return new LinkedHashSet<>(categories);
    }

    private void ensureSkuAvailable(String sku, Integer currentProductId) {
        if (sku == null) {
            return;
        }
        boolean exists = currentProductId == null ? productRepository.existsBySkuIgnoreCase(sku)
            : productRepository.existsBySkuIgnoreCaseAndIdNot(sku, currentProductId);
        if (exists) {
            throw new DuplicateResourceException("Product", "sku", sku);
        }
    }

    private Pageable normalizePageable(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        Sort sort = pageable.getSort();
        if (sort.isUnsorted()) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        for (Sort.Order order : sort) {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new InvalidRequestException(
                    "Unsupported product sort field",
                    Map.of("field", order.getProperty(), "allowedFields", ALLOWED_SORT_FIELDS));
            }
        }
        return PageRequest.of(pageable.getPageNumber(), size, sort);
    }

    private void validatePriceRange(BigDecimal minimumPrice, BigDecimal maximumPrice) {
        if (minimumPrice != null && minimumPrice.signum() < 0) {
            throw new InvalidRequestException("Minimum price cannot be negative");
        }
        if (maximumPrice != null && maximumPrice.signum() < 0) {
            throw new InvalidRequestException("Maximum price cannot be negative");
        }
        if (minimumPrice != null && maximumPrice != null
            && minimumPrice.compareTo(maximumPrice) > 0) {
            throw new InvalidRequestException("Minimum price cannot exceed maximum price");
        }
    }

    private String normalizeOptional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
