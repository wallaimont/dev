package com.orbyt.marketplace.config;

import com.orbyt.marketplace.identity.application.AuthUserDetailsService;
import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            if (!jwtService.isTokenValid(token)) {
                log.warn("[JWT] Token invalid for {} {}", request.getMethod(), request.getServletPath());
                filterChain.doFilter(request, response);
                return;
            }

            UUID userId = jwtService.extractUserId(token);
            UUID tenantId = jwtService.extractTenantId(token);
            AuthUserPrincipal principal = userDetailsService.loadByUserId(userId);
            if (!principal.getTenantId().equals(tenantId)) {
                log.warn("[JWT] Tenant mismatch for {} {}: token={} principal={}", request.getMethod(), request.getServletPath(), tenantId, principal.getTenantId());
                filterChain.doFilter(request, response);
                return;
            }

            TenantContext.set(tenantId);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("[JWT] Authenticated {} {} user={}", request.getMethod(), request.getServletPath(), userId);
        } catch (JwtException ex) {
            log.warn("[JWT] JwtException for {} {}: {}", request.getMethod(), request.getServletPath(), ex.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception ex) {
            log.error("[JWT] Unexpected error for {} {}: {}", request.getMethod(), request.getServletPath(), ex.getMessage(), ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
