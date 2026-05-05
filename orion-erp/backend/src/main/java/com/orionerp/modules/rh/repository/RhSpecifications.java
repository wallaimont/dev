package com.orionerp.modules.rh.repository;

import com.orionerp.modules.rh.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class RhSpecifications {

    private RhSpecifications() {
    }

    public static Specification<Funcionario> funcionarioFilter(
            Long empresaId, Long filialId, Long departamentoId, String situacao, String term) {

        Specification<Funcionario> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));

        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (filialId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("filialId"), filialId));
        }
        if (departamentoId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("departamentoId"), departamentoId));
        }
        if (situacao != null && !situacao.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("situacao"), situacao.toUpperCase()));
        }
        if (term != null && !term.isBlank()) {
            spec = spec.and((root, q, cb) -> {
                String like = "%" + term.trim().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("nome")), like),
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("cpf")), like));
            });
        }
        return spec;
    }

    public static Specification<Cargo> cargoFilter(Long empresaId, String term) {
        Specification<Cargo> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));
        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (term != null && !term.isBlank()) {
            spec = spec.and((root, q, cb) -> {
                String like = "%" + term.trim().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("nome")), like),
                        cb.like(cb.lower(root.get("codigo")), like));
            });
        }
        return spec;
    }

    public static Specification<DepartamentoRh> departamentoRhFilter(Long empresaId, String term) {
        Specification<DepartamentoRh> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));
        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (term != null && !term.isBlank()) {
            spec = spec.and((root, q, cb) -> {
                String like = "%" + term.trim().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("nome")), like),
                        cb.like(cb.lower(root.get("codigo")), like));
            });
        }
        return spec;
    }

    public static Specification<Beneficio> beneficioFilter(Long empresaId, String tipo, String term) {
        Specification<Beneficio> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));
        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (tipo != null && !tipo.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("tipo"), tipo.toUpperCase()));
        }
        if (term != null && !term.isBlank()) {
            spec = spec.and((root, q, cb) -> {
                String like = "%" + term.trim().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("nome")), like),
                        cb.like(cb.lower(root.get("codigo")), like));
            });
        }
        return spec;
    }

    public static Specification<FolhaPagamento> folhaPagamentoFilter(
            Long empresaId, Integer ano, Integer mes, String tipo, String status) {
        Specification<FolhaPagamento> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));
        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (ano != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("ano"), ano));
        }
        if (mes != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("mes"), mes));
        }
        if (tipo != null && !tipo.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("tipo"), tipo.toUpperCase()));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status.toUpperCase()));
        }
        return spec;
    }

    public static Specification<PontoEletronico> pontoEletronicoFilter(
            Long empresaId, Long funcionarioId, LocalDate dataInicio, LocalDate dataFim, String tipo) {
        Specification<PontoEletronico> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));
        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (funcionarioId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("funcionarioId"), funcionarioId));
        }
        if (dataInicio != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("data"), dataInicio));
        }
        if (dataFim != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("data"), dataFim));
        }
        if (tipo != null && !tipo.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("tipo"), tipo.toUpperCase()));
        }
        return spec;
    }

    public static Specification<Ferias> feriasFilter(
            Long empresaId, Long funcionarioId, String status) {
        Specification<Ferias> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("deleted"), false));
        if (empresaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("empresaId"), empresaId));
        }
        if (funcionarioId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("funcionarioId"), funcionarioId));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status.toUpperCase()));
        }
        return spec;
    }
}
