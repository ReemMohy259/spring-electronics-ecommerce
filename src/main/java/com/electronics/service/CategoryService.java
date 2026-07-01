package com.electronics.service;

import com.electronics.dto.category.CategoryInfoResponse;
import com.electronics.dto.category.CategoryRequest;
import com.electronics.dto.category.CategoryResponse;
import com.electronics.entity.Category;
import com.electronics.exception.CategoryNotFoundException;
import com.electronics.exception.DuplicateResourceException;
import com.electronics.exception.ResourceInUseException;
import com.electronics.repository.CategoryRepository;
import com.electronics.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryInfoResponse> findAll() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
            .stream()
            .map(c -> {
                int productsCount = productRepository.countByCategory(c.getId());
                return CategoryInfoResponse.from(c, productsCount);
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public CategoryInfoResponse findById(Integer id) {
        int productsCount = productRepository.countByCategory(id);
        return CategoryInfoResponse.from(getCategory(id), productsCount);
    }

    @Transactional
    public CategoryInfoResponse create(CategoryRequest request) {
        String name = normalizeName(request.name());
        ensureNameAvailable(name, null);

        Category category = new Category();
        category.setName(name);
        return CategoryInfoResponse.from(categoryRepository.save(category), 0);
    }

    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category = getCategory(id);
        String name = normalizeName(request.name());
        ensureNameAvailable(name, id);
        category.setName(name);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Integer id) {
        Category category = getCategory(id);
        if (productRepository.existsByCategoriesId(id)) {
            throw new ResourceInUseException("Category", id);
        }
        categoryRepository.delete(category);
    }

    private Category getCategory(Integer id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    private void ensureNameAvailable(String name, Integer currentId) {
        boolean exists = currentId == null
            ? categoryRepository.existsByNameIgnoreCase(name)
            : categoryRepository.existsByNameIgnoreCaseAndIdNot(name, currentId);
        if (exists) {
            throw new DuplicateResourceException("Category", "name", name);
        }
    }

    private String normalizeName(String name) {
        return name.trim();
    }
}
