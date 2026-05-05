package com.orionerp.modules.fiscal.dto;

import com.orionerp.modules.fiscal.domain.RegraFiscal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RegraFiscalResponse(
        Long id,
        UUID uuid,
        Long empresaId,
        String ufOrigem,
        String ufDestino,
        Long ncmId,
        String ncmCodigo,
        Long cfopId,
        String cfopCodigo,
        Long cstIcmsId,
        String cstIcmsCodigo,
        Long cstPisId,
        String cstPisCodigo,
        Long cstCofinsId,
        String cstCofinsCodigo,
        BigDecimal aliquotaIcms,
        BigDecimal aliquotaPis,
        BigDecimal aliquotaCofins,
        BigDecimal aliquotaIpi,
        BigDecimal reducaoBaseIcms,
        Boolean ativo,
        LocalDateTime createdAt
) {
    public static RegraFiscalResponse from(RegraFiscal r) {
        return new RegraFiscalResponse(
                r.getId(), r.getUuid(), r.getEmpresaId(),
                r.getUfOrigem(), r.getUfDestino(),
                r.getNcm() != null ? r.getNcm().getId() : null,
                r.getNcm() != null ? r.getNcm().getCodigo() : null,
                r.getCfop() != null ? r.getCfop().getId() : null,
                r.getCfop() != null ? r.getCfop().getCodigo() : null,
                r.getCstIcms() != null ? r.getCstIcms().getId() : null,
                r.getCstIcms() != null ? r.getCstIcms().getCodigo() : null,
                r.getCstPis() != null ? r.getCstPis().getId() : null,
                r.getCstPis() != null ? r.getCstPis().getCodigo() : null,
                r.getCstCofins() != null ? r.getCstCofins().getId() : null,
                r.getCstCofins() != null ? r.getCstCofins().getCodigo() : null,
                r.getAliquotaIcms(), r.getAliquotaPis(), r.getAliquotaCofins(),
                r.getAliquotaIpi(), r.getReducaoBaseIcms(),
                r.getAtivo(), r.getCreatedAt()
        );
    }
}
