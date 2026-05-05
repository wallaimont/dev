package com.orionerp.modules.relatorios.dto;

import java.time.LocalDate;

public record RelatorioAniversariantesResponse(
        String funcionarioNome,
        String departamento,
        String cargo,
        LocalDate dataNascimento,
        Integer diaAniversario
) {}
