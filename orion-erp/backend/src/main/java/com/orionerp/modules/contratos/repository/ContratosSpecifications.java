package com.orionerp.modules.contratos.repository;

import com.orionerp.modules.contratos.domain.Contrato;
import org.springframework.data.jpa.domain.Specification;

public final class ContratosSpecifications {

    private ContratosSpecifications() {}

    public static Specification<Contrato> contratoFilter(Long empresaId, Long clienteId, String tipo, String status, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (clienteId != null) p = cb.and(p, cb.equal(root.get("clienteId"), clienteId));
            if (tipo != null && !tipo.isBlank()) p = cb.and(p, cb.equal(root.get("tipo"), tipo));
            if (status != null && !status.isBlank()) p = cb.and(p, cb.equal(root.get("status"), status));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("numero")), like),
                        cb.like(cb.lower(root.get("descricao")), like)
                ));
            }
            return p;
        };
    }
}
