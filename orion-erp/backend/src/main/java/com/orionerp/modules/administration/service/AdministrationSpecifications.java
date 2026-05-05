package com.orionerp.modules.administration.service;

import com.orionerp.modules.administration.domain.Empresa;
import com.orionerp.modules.administration.domain.Filial;
import com.orionerp.modules.administration.domain.ParametroSistema;
import com.orionerp.modules.administration.domain.Perfil;
import com.orionerp.modules.administration.domain.Permissao;
import com.orionerp.modules.administration.domain.Usuario;
import org.springframework.data.jpa.domain.Specification;

public final class AdministrationSpecifications {

    private AdministrationSpecifications() {
    }

    public static Specification<Empresa> empresaFilter(String term) {
        return (root, query, cb) -> {
            var base = cb.isFalse(root.get("deleted"));
            if (term == null || term.isBlank()) {
                return base;
            }
            String like = "%" + term.trim().toLowerCase() + "%";
            return cb.and(base, cb.or(
                    cb.like(cb.lower(root.get("codigo")), like),
                    cb.like(cb.lower(root.get("razaoSocial")), like),
                    cb.like(cb.lower(root.get("nomeFantasia")), like),
                    cb.like(cb.lower(root.get("cnpj")), like)
            ));
        };
    }

    public static Specification<Filial> filialFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var base = cb.isFalse(root.get("deleted"));
            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresa").get("id"), empresaId));
            }
            if (term == null || term.isBlank()) {
                return base;
            }
            String like = "%" + term.trim().toLowerCase() + "%";
            return cb.and(base, cb.or(
                    cb.like(cb.lower(root.get("codigo")), like),
                    cb.like(cb.lower(root.get("razaoSocial")), like),
                    cb.like(cb.lower(root.get("nomeFantasia")), like),
                    cb.like(cb.lower(root.get("cnpj")), like)
            ));
        };
    }

    public static Specification<Usuario> usuarioFilter(Long empresaId, Long filialId, String term) {
        return (root, query, cb) -> {
            var base = cb.isFalse(root.get("deleted"));
            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresaId"), empresaId));
            }
            if (filialId != null) {
                base = cb.and(base, cb.equal(root.get("filialId"), filialId));
            }
            if (term == null || term.isBlank()) {
                return base;
            }
            String like = "%" + term.trim().toLowerCase() + "%";
            return cb.and(base, cb.or(
                    cb.like(cb.lower(root.get("nome")), like),
                    cb.like(cb.lower(root.get("email")), like)
            ));
        };
    }

    public static Specification<Perfil> perfilFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var base = cb.isFalse(root.get("deleted"));
            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresaId"), empresaId));
            }
            if (term == null || term.isBlank()) {
                return base;
            }
            String like = "%" + term.trim().toLowerCase() + "%";
            return cb.and(base, cb.or(
                    cb.like(cb.lower(root.get("codigo")), like),
                    cb.like(cb.lower(root.get("nome")), like)
            ));
        };
    }

    public static Specification<Permissao> permissaoFilter(String modulo, String term) {
        return (root, query, cb) -> {
            var base = cb.conjunction();
            if (modulo != null && !modulo.isBlank()) {
                base = cb.and(base, cb.equal(cb.lower(root.get("modulo")), modulo.trim().toLowerCase()));
            }
            if (term == null || term.isBlank()) {
                return base;
            }
            String like = "%" + term.trim().toLowerCase() + "%";
            return cb.and(base, cb.or(
                    cb.like(cb.lower(root.get("modulo")), like),
                    cb.like(cb.lower(root.get("recurso")), like),
                    cb.like(cb.lower(root.get("acao")), like),
                    cb.like(cb.lower(root.get("descricao")), like)
            ));
        };
    }

    public static Specification<ParametroSistema> parametroFilter(Long empresaId, Long filialId, String modulo, String term) {
        return (root, query, cb) -> {
            var base = cb.isFalse(root.get("deleted"));
            if (empresaId != null) {
                base = cb.and(base, cb.equal(root.get("empresaId"), empresaId));
            }
            if (filialId != null) {
                base = cb.and(base, cb.equal(root.get("filialId"), filialId));
            }
            if (modulo != null && !modulo.isBlank()) {
                base = cb.and(base, cb.equal(cb.lower(root.get("modulo")), modulo.trim().toLowerCase()));
            }
            if (term == null || term.isBlank()) {
                return base;
            }
            String like = "%" + term.trim().toLowerCase() + "%";
            return cb.and(base, cb.or(
                    cb.like(cb.lower(root.get("chave")), like),
                    cb.like(cb.lower(root.get("descricao")), like),
                    cb.like(cb.lower(root.get("valor")), like)
            ));
        };
    }
}
