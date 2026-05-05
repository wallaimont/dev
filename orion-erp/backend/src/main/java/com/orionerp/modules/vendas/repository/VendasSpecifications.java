package com.orionerp.modules.vendas.repository;

import com.orionerp.modules.vendas.domain.PedidoVenda;
import org.springframework.data.jpa.domain.Specification;

public final class VendasSpecifications {

    private VendasSpecifications() {}

    public static Specification<PedidoVenda> pedidoVendaFilter(
            Long empresaId, Long filialId, Long clienteId, Long vendedorId, String status, String term) {

        Specification<PedidoVenda> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));

        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (filialId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("filialId"), filialId));
        }
        if (clienteId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("cliente").get("id"), clienteId));
        }
        if (vendedorId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("vendedorId"), vendedorId));
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
}
