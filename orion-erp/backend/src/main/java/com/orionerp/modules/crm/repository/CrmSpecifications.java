package com.orionerp.modules.crm.repository;

import com.orionerp.modules.crm.domain.Lead;
import com.orionerp.modules.crm.domain.Oportunidade;
import org.springframework.data.jpa.domain.Specification;

public final class CrmSpecifications {

    private CrmSpecifications() {
    }

    public static Specification<Lead> leadFilter(Long empresaId, String status,
                                                 Long responsavelId, String term) {

        Specification<Lead> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));

        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status.toUpperCase()));
        }
        if (responsavelId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("responsavelId"), responsavelId));
        }
        if (term != null && !term.isBlank()) {
            spec = spec.and((root, q, cb) -> {
                String like = "%" + term.trim().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("nome")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("empresaLead")), like));
            });
        }
        return spec;
    }

    public static Specification<Oportunidade> oportunidadeFilter(Long empresaId, String etapaFunil,
                                                                  Long responsavelId, String term) {

        Specification<Oportunidade> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));

        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (etapaFunil != null && !etapaFunil.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("etapaFunil"), etapaFunil.toUpperCase()));
        }
        if (responsavelId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("responsavelId"), responsavelId));
        }
        if (term != null && !term.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("titulo")),
                    "%" + term.trim().toLowerCase() + "%"));
        }
        return spec;
    }
}
