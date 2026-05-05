package com.nexus.modules.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    public String       accessToken;
    public String       refreshToken;
    public String       tokenType = "Bearer";
    public long         expiresIn;
    public UUID         userId;
    public UUID         tenantId;
    public String       email;
    public List<String> roles;
}
