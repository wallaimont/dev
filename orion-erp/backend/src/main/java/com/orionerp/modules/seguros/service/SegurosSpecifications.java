package com.orionerp.modules.seguros.service;

import com.orionerp.modules.seguros.domain.Seguradora;
import com.orionerp.modules.seguros.domain.Corretora;
import com.orionerp.modules.seguros.domain.PropostaSeguro;
import com.orionerp.modules.seguros.domain.Apolice;
import com.orionerp.modules.seguros.domain.NotificacaoSeguro;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class SegurosSpecifications {

    private SegurosSpecifications() {}

    public static Specification<Seguradora> seguradoraFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));
            if (empresaId != null) predicates.add(cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                String like = "%" + term.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nome")), like),
                        cb.like(cb.lower(root.get("cnpj")), like),
                        cb.like(cb.lower(root.get("codigo")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Corretora> corretoraFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));
            if (empresaId != null) predicates.add(cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                String like = "%" + term.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nome")), like),
                        cb.like(cb.lower(root.get("cnpj")), like),
                        cb.like(cb.lower(root.get("codigo")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<PropostaSeguro> propostaFilter(Long empresaId, Long clienteId, Long seguradoraId, String status, String term) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));
            if (empresaId != null) predicates.add(cb.equal(root.get("empresaId"), empresaId));
            if (clienteId != null) predicates.add(cb.equal(root.get("clienteId"), clienteId));
            if (seguradoraId != null) predicates.add(cb.equal(root.get("seguradoraId"), seguradoraId));
            if (status != null && !status.isBlank()) predicates.add(cb.equal(root.get("status"), status));
            if (term != null && !term.isBlank()) {
                String like = "%" + term.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("numero")), like),
                        cb.like(cb.lower(root.get("ramo")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Apolice> apoliceFilter(Long empresaId, Long clienteId, Long seguradoraId, String status, String term) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));
            if (empresaId != null) predicates.add(cb.equal(root.get("empresaId"), empresaId));
            if (clienteId != null) predicates.add(cb.equal(root.get("clienteId"), clienteId));
            if (seguradoraId != null) predicates.add(cb.equal(root.get("seguradoraId"), seguradoraId));
            if (status != null && !status.isBlank()) predicates.add(cb.equal(root.get("status"), status));
            if (term != null && !term.isBlank()) {
                String like = "%" + term.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("numero")), like),
                        cb.like(cb.lower(root.get("ramo")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<NotificacaoSeguro> notificacaoFilter(Long empresaId, Boolean lida, String tipo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (empresaId != null) predicates.add(cb.equal(root.get("empresaId"), empresaId));
            if (lida != null) predicates.add(cb.equal(root.get("lida"), lida));
            if (tipo != null && !tipo.isBlank()) predicates.add(cb.equal(root.get("tipo"), tipo));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<NotificacaoSeguro> notificacaoEmpresa(Long empresaId) {
        return (root, query, cb) -> empresaId == null ? null : cb.equal(root.get("empresaId"), empresaId);
    }

    public static Specification<NotificacaoSeguro> notificacaoLida(Boolean lida) {
        return (root, query, cb) -> lida == null ? null : cb.equal(root.get("lida"), lida);
    }
}
