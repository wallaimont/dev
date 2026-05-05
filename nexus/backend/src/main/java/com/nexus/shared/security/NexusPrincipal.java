package com.nexus.shared.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class NexusPrincipal {
    private final UUID userId;
    private final UUID tenantId;
}
