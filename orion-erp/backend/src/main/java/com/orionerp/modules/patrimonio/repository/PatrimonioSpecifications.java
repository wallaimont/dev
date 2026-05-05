package com.orionerp.modules.patrimonio.repository;

import com.orionerp.modules.patrimonio.domain.BemPatrimonial;
import com.orionerp.modules.patrimonio.domain.Depreciacao;
import org.springframework.data.jpa.domain.Specification;

public final class PatrimonioSpecifications {

    private PatrimonioSpecifications() {}

    public static Specification<BemPatrimonial> bemPatrimonialFilter(Long empresaId, String grupo, String status, String term) {
        Specification<BemPatrimonial> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);

        if (empresaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (grupo != null && !grupo.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("grupo"), grupo));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (term != null && !term.isBlank()) {
            String like = "%" + term.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("codigo")), like),
                    cb.like(cb.lower(root.get("descricao")), like),
                    cb.like(cb.lower(root.get("numeroPatrimonio")), like)
            ));
        }
        return spec;
    }

    public static Specification<Depreciacao> depreciacaoFilter(Long empresaId, Long bemPatrimonialId, Integer ano, Integer mes) {
        Specification<Depreciacao> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);

        if (empresaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (bemPatrimonialId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("bemPatrimonialId"), bemPatrimonialId));
        }
        if (ano != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("anoReferencia"), ano));
        }
        if (mes != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("mesReferencia"), mes));
        }
        return spec;
    }
}
