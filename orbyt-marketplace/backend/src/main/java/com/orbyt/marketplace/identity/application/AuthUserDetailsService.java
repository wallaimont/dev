package com.orbyt.marketplace.identity.application;

import com.orbyt.marketplace.identity.domain.User;
import com.orbyt.marketplace.identity.repository.PermissionQueryRepository;
import com.orbyt.marketplace.identity.repository.UserRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PermissionQueryRepository permissionQueryRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID tenantId = TenantContext.require();
        User user = userRepository.findByTenantIdAndEmailIgnoreCase(tenantId, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toPrincipal(user);
    }

    public AuthUserPrincipal loadByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toPrincipal(user);
    }

    private AuthUserPrincipal toPrincipal(User user) {
        var permissions = permissionQueryRepository.findPermissionsByUserIdAndTenantId(user.getId(), user.getTenantId());
        return AuthUserPrincipal.builder()
                .userId(user.getId())
                .tenantId(user.getTenantId())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(permissions)
                .build();
    }
}
