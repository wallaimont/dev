package com.nexus.modules.user.domain;

public enum UserStatus {
    ACTIVE, INACTIVE, PENDING_VERIFICATION, SUSPENDED, BANNED;

    public boolean isActive() { return this == ACTIVE; }
    public boolean canLogin() { return this == ACTIVE || this == PENDING_VERIFICATION; }
}
