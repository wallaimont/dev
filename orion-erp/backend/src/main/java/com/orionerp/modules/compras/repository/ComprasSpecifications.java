package com.orionerp.modules.compras.repository;

import com.orionerp.modules.compras.domain.PedidoCompra;
import com.orionerp.modules.compras.domain.Recebimento;
import org.springframework.data.jpa.domain.Specification;

public final class ComprasSpecifications {

    private ComprasSpecifications() {}

    public static Specification<PedidoCompra> pedidoCompraFilter(
            Long empresaId, Long filialId, Long fornecedorId, String status, String term) {

        Specification<PedidoCompra> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));

        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (filialId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("filialId"), filialId));
        }
        if (fornecedorId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("fornecedor").get("id"), fornecedorId));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status.toUpperCase()));
        }
        if (term != null && !term.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("numero")),
                    "%" + term.toLowerCase() + "%"));
        }
        return spec;
    }

    public static Specification<Recebimento> recebimentoFilter(
            Long empresaId, Long filialId, Long fornecedorId, String status) {

        Specification<Recebimento> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));

        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (filialId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("filialId"), filialId));
        }
        if (fornecedorId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("fornecedor").get("id"), fornecedorId));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status.toUpperCase()));
        }
        return spec;
    }
}
