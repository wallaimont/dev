package com.supportdesk.security;

import com.supportdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPrincipalService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrId) throws UsernameNotFoundException {
        // Accepts both email (login) and UUID string (JWT filter lookup)
        try {
            var id = java.util.UUID.fromString(emailOrId);
            return userRepository.findById(id)
                    .map(UserPrincipal::from)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + emailOrId));
        } catch (IllegalArgumentException e) {
            return userRepository.findByEmailWithRolesAndPermissions(emailOrId)
                    .map(UserPrincipal::from)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + emailOrId));
        }
    }
}
