package com.orionerp.modules.administration.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        Long filialId,
        Long perfilId,
        String perfilNome,
        String nome,
        String email,
        String telefone,
        Boolean trocarSenha,
        Boolean bloqueado,
        LocalDateTime bloqueadoAte,
        LocalDateTime ultimoLogin,
        Boolean ativo
) {
}
