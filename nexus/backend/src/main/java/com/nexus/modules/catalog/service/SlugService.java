package com.nexus.modules.catalog.service;

import com.nexus.modules.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlugService {

    private final ProductRepository productRepository;

    public String generateUniqueSlug(String name, UUID tenantId) {
        String base = toSlug(name);
        String slug = base;
        int counter = 1;

        while (productRepository.existsByTenantIdAndSlugAndDeletedAtIsNull(tenantId, slug)) {
            slug = base + "-" + counter++;
        }
        return slug;
    }

    private String toSlug(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
            .toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .trim()
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-");
    }
}
