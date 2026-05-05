package com.orionerp.modules.estoque.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record InventarioRequest(
        @NotNull Long empresaId,
        @NotNull Long filialId,
        @NotNull Long armazemId,
        @NotBlank @Size(max = 20) String numero,
        LocalDate dataInventario,
        Long responsavelId,
        String observacao,
        List<ItemInput> itens
) {
    public record ItemInput(
            @NotNull Long produtoId,
            String lote
    ) {
    }
}
