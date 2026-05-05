package com.orionerp.modules.administration.dto;

public record MenuDto(
        Long id,
        String codigo,
        String titulo,
        String icone,
        String rota,
        Integer ordem,
        String modulo,
        String permissaoRecurso,
        Long parentId
) {
}
