package com.orbyt.marketplace.identity.repository;

import com.orbyt.marketplace.identity.domain.UserAddress;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    List<UserAddress> findByUserIdOrderByDefaultAddressDesc(UUID userId);
}
