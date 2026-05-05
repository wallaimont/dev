package com.nexus.modules.order.service;

import com.nexus.modules.seller.domain.Seller;
import com.nexus.modules.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CommissionCalculator {

    private final SellerRepository sellerRepository;
    private final RedisTemplate<String, String> redis;

    private static final BigDecimal DEFAULT_RATE = new BigDecimal("0.10");

    public CommissionCalculator(
        SellerRepository sellerRepository,
        @Qualifier("redisTemplate") RedisTemplate<String, String> redis
    ) {
        this.sellerRepository = sellerRepository;
        this.redis = redis;
    }

    public BigDecimal getRate(UUID tenantId, UUID sellerId) {
        String cacheKey = "commission:" + tenantId + ":" + sellerId;
        String cached = redis.opsForValue().get(cacheKey);
        if (cached != null) return new BigDecimal(cached);

        BigDecimal rate = sellerRepository.findById(sellerId)
            .map(Seller::getCommissionRate)
            .filter(r -> r != null && r.compareTo(BigDecimal.ZERO) > 0)
            .orElse(DEFAULT_RATE);

        redis.opsForValue().set(cacheKey, rate.toPlainString(), 5, TimeUnit.MINUTES);
        return rate;
    }
}
