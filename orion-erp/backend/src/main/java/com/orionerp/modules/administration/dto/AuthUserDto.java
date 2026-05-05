package com.orionerp.modules.administration.dto;

import java.util.List;

public record AuthUserDto(
        Long id,
        Long empresaId,
        Long filialId,
        String nome,
        String email,
        String perfil,
        List<String> permissoes
) {
}
