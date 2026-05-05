package com.supportdesk.service;

import com.supportdesk.domain.entity.*;
import com.supportdesk.dto.request.CreateCategoryRequest;
import com.supportdesk.dto.response.CategoryResponse;
import com.supportdesk.exception.*;
import com.supportdesk.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Cacheable(value = "categories")
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findByActiveTrue().stream().map(this::toResponse).toList();
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public CategoryResponse create(CreateCategoryRequest req) {
        if (categoryRepository.existsByNameIgnoreCase(req.getName())) {
            throw new BusinessException("CATEGORY_EXISTS", "Category with this name already exists");
        }
        Category category = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .active(true)
                .build();
        return toResponse(categoryRepository.save(category));
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId()).name(c.getName()).description(c.getDescription()).active(c.isActive())
                .build();
    }
}
