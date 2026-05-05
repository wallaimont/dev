package com.orbyt.marketplace.engagement.realtime;

import com.orbyt.marketplace.catalog.repository.SellerRepository;
import com.orbyt.marketplace.identity.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTargetResolver {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    public Optional<UUID> resolveUserId(UUID candidateId) {
        if (candidateId == null) {
            return Optional.empty();
        }
        if (userRepository.existsById(candidateId)) {
            return Optional.of(candidateId);
        }
        return sellerRepository.findById(candidateId).map(seller -> seller.getUserId());
    }

    public Optional<String> resolveUsername(UUID candidateId) {
        return resolveUserId(candidateId)
                .flatMap(userRepository::findById)
                .map(user -> user.getEmail());
    }
}
