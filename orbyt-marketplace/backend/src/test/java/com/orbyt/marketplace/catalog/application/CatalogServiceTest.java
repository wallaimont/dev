package com.orbyt.marketplace.catalog.application;

import com.orbyt.marketplace.catalog.api.dto.UpsertProductRequest;
import com.orbyt.marketplace.catalog.domain.Product;
import com.orbyt.marketplace.catalog.repository.ProductRepository;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private CatalogService catalogService;

    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.set(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldCreateApprovedProductWithUppercaseSku() {
        UpsertProductRequest request = new UpsertProductRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "orb-001",
                "Dock station",
                "Dock para notebook",
                new BigDecimal("299.90"),
                new BigDecimal("249.90"),
                "BRL"
        );

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        catalogService.create(request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getTenantId()).isEqualTo(tenantId);
        assertThat(productCaptor.getValue().getSku()).isEqualTo("ORB-001");
        assertThat(productCaptor.getValue().getApprovalStatus()).isEqualTo("APPROVED");
        assertThat(productCaptor.getValue().getPrice()).isEqualByComparingTo("299.90");
    }
}