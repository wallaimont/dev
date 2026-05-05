package com.orionerp.modules.estoque.service;

import com.orionerp.modules.estoque.domain.Armazem;
import com.orionerp.modules.estoque.domain.Inventario;
import com.orionerp.modules.estoque.domain.MovimentacaoEstoque;
import com.orionerp.modules.estoque.domain.SaldoEstoque;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class EstoqueSpecifications {

    private EstoqueSpecifications() {
    }

    public static Specification<Armazem> armazemFilter(Long empresaId, Long filialId, String term) {
        return (root, query, cb) -> {
            var base = cb.isFalse(root.get("deleted"));
            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresaId"), empresaId));
            }
            if (filialId != null) {
                base = cb.and(base, cb.equal(root.get("filialId"), filialId));
            }
            if (term != null && !term.isBlank()) {
                String like = "%" + term.trim().toLowerCase() + "%";
                base = cb.and(base, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like)));
            }
            return base;
        };
    }

    public static Specification<MovimentacaoEstoque> movimentacaoFilter(Long empresaId, Long filialId,
                                                                       Long armazemId, Long produtoId,
                                                                       String tipo,
                                                                       LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, cb) -> {
            var base = cb.conjunction();
            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresaId"), empresaId));
            }
            if (filialId != null) {
                base = cb.and(base, cb.equal(root.get("filialId"), filialId));
            }
            if (armazemId != null) {
                base = cb.and(base, cb.equal(root.get("armazem").get("id"), armazemId));
            }
            if (produtoId != null) {
                base = cb.and(base, cb.equal(root.get("produto").get("id"), produtoId));
            }
            if (tipo != null && !tipo.isBlank()) {
                base = cb.and(base, cb.equal(root.get("tipo"), tipo.trim().toUpperCase()));
            }
            if (dataInicio != null) {
                base = cb.and(base, cb.greaterThanOrEqualTo(root.get("createdAt"),
                        dataInicio.atStartOfDay()));
            }
            if (dataFim != null) {
                base = cb.and(base, cb.lessThanOrEqualTo(root.get("createdAt"),
                        dataFim.plusDays(1).atStartOfDay()));
            }
            return base;
        };
    }

    public static Specification<SaldoEstoque> saldoFilter(Long empresaId, Long filialId,
                                                          Long armazemId, Long produtoId) {
        return (root, query, cb) -> {
            var base = cb.conjunction();
            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresaId"), empresaId));
            }
            if (filialId != null) {
                base = cb.and(base, cb.equal(root.get("filialId"), filialId));
            }
            if (armazemId != null) {
                base = cb.and(base, cb.equal(root.get("armazem").get("id"), armazemId));
            }
            if (produtoId != null) {
                base = cb.and(base, cb.equal(root.get("produto").get("id"), produtoId));
            }
            return base;
        };
    }

    public static Specification<Inventario> inventarioFilter(Long empresaId, Long filialId,
                                                             Long armazemId, String status, String term) {
        return (root, query, cb) -> {
            var base = cb.isFalse(root.get("deleted"));
            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresaId"), empresaId));
            }
            if (filialId != null) {
                base = cb.and(base, cb.equal(root.get("filialId"), filialId));
            }
            if (armazemId != null) {
                base = cb.and(base, cb.equal(root.get("armazem").get("id"), armazemId));
            }
            if (status != null && !status.isBlank()) {
                base = cb.and(base, cb.equal(root.get("status"), status.trim().toUpperCase()));
            }
            if (term != null && !term.isBlank()) {
                String like = "%" + term.trim().toLowerCase() + "%";
                base = cb.and(base, cb.like(cb.lower(root.get("numero")), like));
            }
            return base;
        };
    }
}
