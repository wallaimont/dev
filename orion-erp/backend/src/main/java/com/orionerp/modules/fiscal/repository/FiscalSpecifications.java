package com.orionerp.modules.fiscal.repository;

import com.orionerp.modules.fiscal.domain.NaturezaOperacao;
import com.orionerp.modules.fiscal.domain.RegraFiscal;
import org.springframework.data.jpa.domain.Specification;

public final class FiscalSpecifications {

    private FiscalSpecifications() {
    }

    public static Specification<NaturezaOperacao> naturezaOperacaoFilter(
            Long empresaId, String tipo, String term) {

        Specification<NaturezaOperacao> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));

        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (tipo != null && !tipo.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("tipo"), tipo.toUpperCase()));
        }
        if (term != null && !term.isBlank()) {
            spec = spec.and((root, q, cb) -> {
                String like = "%" + term.trim().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like));
            });
        }
        return spec;
    }

    public static Specification<RegraFiscal> regraFiscalFilter(
            Long empresaId, String ufOrigem, String ufDestino, Long ncmId) {

        Specification<RegraFiscal> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("ativo"), true));

        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (ufOrigem != null && !ufOrigem.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("ufOrigem"), ufOrigem.toUpperCase()));
        }
        if (ufDestino != null && !ufDestino.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("ufDestino"), ufDestino.toUpperCase()));
        }
        if (ncmId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("ncm").get("id"), ncmId));
        }
        return spec;
    }
}
