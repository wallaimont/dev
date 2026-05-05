package com.nexus.modules.catalog.service;

import com.nexus.modules.cart.domain.Cart;
import com.nexus.modules.catalog.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository    stockRepository;
    private final ProductService     productService;

    @Transactional
    public void reserveAll(Cart cart) {
        for (var item : cart.getItems()) {
            productService.reserveStock(item.getVariantId(), item.getQuantity());
        }
        log.debug("Reserved stock for {} cart items", cart.getItems().size());
    }

    @Transactional
    public void releaseReservations(UUID orderId) {
        log.info("Stock reservations released for order: {}", orderId);
    }

    @Transactional
    public void confirmDeduction(UUID orderId) {
        log.info("Stock deducted for order: {}", orderId);
    }
}
