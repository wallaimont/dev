package com.orionerp.modules.pcp.repository;

import com.orionerp.modules.pcp.domain.ApontamentoProducao;
import com.orionerp.modules.pcp.domain.EstruturaProduto;
import com.orionerp.modules.pcp.domain.OrdemProducao;
import org.springframework.data.jpa.domain.Specification;

public final class PcpSpecifications {

    private PcpSpecifications() {}

    public static Specification<EstruturaProduto> estruturaProdutoFilter(Long empresaId, Long produtoPaiId) {
        Specification<EstruturaProduto> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);
        if (empresaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (produtoPaiId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("produtoPaiId"), produtoPaiId));
        }
        return spec;
    }

    public static Specification<OrdemProducao> ordemProducaoFilter(Long empresaId, Long produtoId, String status, String prioridade, String term) {
        Specification<OrdemProducao> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);
        if (empresaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (produtoId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("produtoId"), produtoId));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (prioridade != null && !prioridade.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("prioridade"), prioridade));
        }
        if (term != null && !term.isBlank()) {
            String like = "%" + term.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("numero")), like));
        }
        return spec;
    }

    public static Specification<ApontamentoProducao> apontamentoFilter(Long empresaId, Long ordemProducaoId, Long funcionarioId) {
        Specification<ApontamentoProducao> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);
        if (empresaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (ordemProducaoId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ordemProducaoId"), ordemProducaoId));
        }
        if (funcionarioId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("funcionarioId"), funcionarioId));
        }
        return spec;
    }
}
