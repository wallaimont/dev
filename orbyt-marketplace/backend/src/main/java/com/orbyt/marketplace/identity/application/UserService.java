package com.orbyt.marketplace.identity.application;

import com.orbyt.marketplace.identity.api.dto.UpsertUserRequest;
import com.orbyt.marketplace.identity.api.dto.UserResponse;
import com.orbyt.marketplace.identity.domain.User;
import com.orbyt.marketplace.identity.repository.UserRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        UUID tenantId = requireTenantId();
        return userRepository.findAllByTenantIdAndDeletedAtIsNull(tenantId).stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(UUID id) {
        UUID tenantId = requireTenantId();
        User user = userRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    @Transactional
    public UserResponse create(UpsertUserRequest request) {
        UUID tenantId = requireTenantId();
        userRepository.findByTenantIdAndEmailIgnoreCase(tenantId, request.email())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists in tenant");
                });
        User user = new User();
        user.setTenantId(tenantId);
        apply(user, request, true);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(UUID id, UpsertUserRequest request) {
        UUID tenantId = requireTenantId();
        User user = userRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.findByTenantIdAndEmailIgnoreCase(tenantId, request.email())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists in tenant");
                });
        apply(user, request, false);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = requireTenantId();
        User user = userRepository.findByIdAndTenantIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setStatus("INACTIVE");
        user.setDeletedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    private void apply(User user, UpsertUserRequest request, boolean creating) {
        user.setEmail(request.email().toLowerCase(Locale.ROOT));
        user.setFullName(request.fullName());
        user.setPreferredLocale(request.preferredLocale());
        user.setPreferredCurrency(request.preferredCurrency().toUpperCase(Locale.ROOT));
        user.setStatus("ACTIVE");
        if (creating || (request.password() != null && !request.password().isBlank())) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getTenantId(),
                user.getEmail(),
                user.getFullName(),
                user.getPreferredLocale(),
                user.getPreferredCurrency(),
                user.getStatus()
        );
    }

    private UUID requireTenantId() {
        UUID tenantId = TenantContext.get();
        if (tenantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-Tenant-Id header is required");
        }
        return tenantId;
    }
}
