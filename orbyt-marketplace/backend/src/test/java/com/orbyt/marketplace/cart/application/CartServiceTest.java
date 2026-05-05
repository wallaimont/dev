package com.orbyt.marketplace.cart.application;

import com.orbyt.marketplace.cart.api.dto.AddToCartRequest;
import com.orbyt.marketplace.cart.api.dto.CartResponse;
import com.orbyt.marketplace.cart.domain.Cart;
import com.orbyt.marketplace.cart.repository.CartRepository;
import com.orbyt.marketplace.catalog.domain.Product;
import com.orbyt.marketplace.catalog.domain.Store;
import com.orbyt.marketplace.catalog.repository.ProductRepository;
import com.orbyt.marketplace.catalog.repository.StoreRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private CartService cartService;

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
    void shouldAddItemUsingPromotionalPrice() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);
        product.setStoreId(UUID.randomUUID());
        product.setPrice(new BigDecimal("100.00"));
        product.setPromotionalPrice(new BigDecimal("80.00"));
        product.setCurrencyCode("BRL");

        Store store = new Store();
        store.setSellerId(UUID.randomUUID());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(storeRepository.findById(product.getStoreId())).thenReturn(Optional.of(store));
        when(cartRepository.findByTenantIdAndUserId(tenantId, userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addItem(userId, new AddToCartRequest(productId, null, 2));

        assertThat(response.currencyCode()).isEqualTo("BRL");
        assertThat(response.subtotal()).isEqualByComparingTo("160.00");
        assertThat(response.total()).isEqualByComparingTo("160.00");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().unitPrice()).isEqualByComparingTo("80.00");
        assertThat(response.items().getFirst().subtotal()).isEqualByComparingTo("160.00");
    }
}