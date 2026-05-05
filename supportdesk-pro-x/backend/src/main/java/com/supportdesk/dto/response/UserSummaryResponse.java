package com.supportdesk.dto.response;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserSummaryResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private Set<String> roles;
}
