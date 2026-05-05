package com.sigaseguros.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {

    private Long id;
    private String nome;
    private String email;
    private String perfil;
    private List<String> modules;
    private Map<String, List<String>> permissions;
}
