package com.electronics.service;

import com.electronics.dto.CategoryRequest;
import com.electronics.dto.CategoryResponse;
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
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
            .stream()
            .map(CategoryResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Integer id) {
        return CategoryResponse.from(getCategory(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = normalizeName(request.name());
        ensureNameAvailable(name, null);

        Category category = new Category();
        category.setName(name);
        category.setSlug(request.slug());
        category.setLucideIconName(request.lucideIconName());
        return CategoryResponse.from(categoryRepository.save(category));
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
        boolean exists = currentId == null ? categoryRepository.existsByNameIgnoreCase(name)
            : categoryRepository.existsByNameIgnoreCaseAndIdNot(name, currentId);
        if (exists) {
            throw new DuplicateResourceException("Category", "name", name);
        }
    }

    private String normalizeName(String name) {
        return name.trim();
    }
}
