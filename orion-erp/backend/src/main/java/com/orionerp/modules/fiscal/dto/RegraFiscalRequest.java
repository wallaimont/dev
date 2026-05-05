package com.orionerp.modules.fiscal.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegraFiscalRequest(
        @NotNull Long empresaId,
        String ufOrigem,
        String ufDestino,
        Long ncmId,
        Long cfopId,
        Long cstIcmsId,
        Long cstPisId,
        Long cstCofinsId,
        BigDecimal aliquotaIcms,
        BigDecimal aliquotaPis,
        BigDecimal aliquotaCofins,
        BigDecimal aliquotaIpi,
        BigDecimal reducaoBaseIcms
) {
}
