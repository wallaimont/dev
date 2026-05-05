package com.orionerp.modules.financeiro.repository;

import com.orionerp.modules.financeiro.domain.ConciliacaoBancaria;
import org.springframework.data.jpa.domain.Specification;

public final class FinanceiroSpecifications {

    private FinanceiroSpecifications() {}

    public static Specification<ConciliacaoBancaria> conciliacaoFilter(Long empresaId, Long contaBancariaId, String status) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (contaBancariaId != null) p = cb.and(p, cb.equal(root.get("contaBancariaId"), contaBancariaId));
            if (status != null && !status.isBlank()) p = cb.and(p, cb.equal(root.get("status"), status));
            return p;
        };
    }
}
