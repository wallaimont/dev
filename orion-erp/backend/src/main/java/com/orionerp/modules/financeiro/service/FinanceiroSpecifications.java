package com.orionerp.modules.financeiro.service;

import com.orionerp.modules.financeiro.domain.FluxoCaixa;
import com.orionerp.modules.financeiro.domain.Titulo;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class FinanceiroSpecifications {

    private FinanceiroSpecifications() {
    }

    public static Specification<Titulo> tituloFilter(Long empresaId, Long filialId, String tipo,
                                                     String status, Long clienteId, Long fornecedorId,
                                                     LocalDate dataInicio, LocalDate dataFim, String term) {
        return (root, query, cb) -> {
            var base = cb.isFalse(root.get("deleted"));

            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresaId"), empresaId));
            }
            if (filialId != null) {
                base = cb.and(base, cb.equal(root.get("filialId"), filialId));
            }
            if (tipo != null && !tipo.isBlank()) {
                base = cb.and(base, cb.equal(root.get("tipo"), tipo.trim().toUpperCase()));
            }
            if (status != null && !status.isBlank()) {
                base = cb.and(base, cb.equal(root.get("status"), status.trim().toUpperCase()));
            }
            if (clienteId != null) {
                base = cb.and(base, cb.equal(root.get("cliente").get("id"), clienteId));
            }
            if (fornecedorId != null) {
                base = cb.and(base, cb.equal(root.get("fornecedor").get("id"), fornecedorId));
            }
            if (dataInicio != null) {
                base = cb.and(base, cb.greaterThanOrEqualTo(root.get("dataEmissao"), dataInicio));
            }
            if (dataFim != null) {
                base = cb.and(base, cb.lessThanOrEqualTo(root.get("dataEmissao"), dataFim));
            }
            if (term != null && !term.isBlank()) {
                String like = "%" + term.trim().toLowerCase() + "%";
                base = cb.and(base, cb.or(
                        cb.like(cb.lower(root.get("numero")), like),
                        cb.like(cb.lower(root.get("observacao")), like)
                ));
            }
            return base;
        };
    }

    public static Specification<FluxoCaixa> fluxoCaixaFilter(Long empresaId, Long filialId,
                                                             Long contaBancariaId, String tipo,
                                                             LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, cb) -> {
            var base = cb.conjunction();

            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresaId"), empresaId));
            }
            if (filialId != null) {
                base = cb.and(base, cb.equal(root.get("filialId"), filialId));
            }
            if (contaBancariaId != null) {
                base = cb.and(base, cb.equal(root.get("contaBancaria").get("id"), contaBancariaId));
            }
            if (tipo != null && !tipo.isBlank()) {
                base = cb.and(base, cb.equal(root.get("tipo"), tipo.trim().toUpperCase()));
            }
            if (dataInicio != null) {
                base = cb.and(base, cb.greaterThanOrEqualTo(root.get("dataLancamento"), dataInicio));
            }
            if (dataFim != null) {
                base = cb.and(base, cb.lessThanOrEqualTo(root.get("dataLancamento"), dataFim));
            }
            return base;
        };
    }
}
