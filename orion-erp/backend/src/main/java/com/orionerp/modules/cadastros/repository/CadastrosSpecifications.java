package com.orionerp.modules.cadastros.repository;

import com.orionerp.modules.cadastros.domain.*;
import org.springframework.data.jpa.domain.Specification;

public final class CadastrosSpecifications {

    private CadastrosSpecifications() {}

    public static Specification<Categoria> categoriaFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like)));
            }
            return p;
        };
    }

    public static Specification<CentroCusto> centroCustoFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like)));
            }
            return p;
        };
    }

    public static Specification<Cliente> clienteFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("razaoSocial")), like),
                        cb.like(cb.lower(root.get("nomeFantasia")), like),
                        cb.like(cb.lower(root.get("cpfCnpj")), like)));
            }
            return p;
        };
    }

    public static Specification<Fornecedor> fornecedorFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("razaoSocial")), like),
                        cb.like(cb.lower(root.get("cpfCnpj")), like)));
            }
            return p;
        };
    }

    public static Specification<GrupoProduto> grupoProdutoFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like)));
            }
            return p;
        };
    }

    public static Specification<Marca> marcaFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like)));
            }
            return p;
        };
    }

    public static Specification<SubgrupoProduto> subgrupoProdutoFilter(Long empresaId, Long grupoId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (grupoId != null) p = cb.and(p, cb.equal(root.get("grupo").get("id"), grupoId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like)));
            }
            return p;
        };
    }

    public static Specification<NaturezaFinanceira> naturezaFinanceiraFilter(Long empresaId, String tipo, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (tipo != null && !tipo.isBlank()) p = cb.and(p, cb.equal(root.get("tipo"), tipo));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like)));
            }
            return p;
        };
    }

    public static Specification<CondicaoPagamento> condicaoPagamentoFilter(Long empresaId, String tipo, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (tipo != null && !tipo.isBlank()) p = cb.and(p, cb.equal(root.get("tipo"), tipo));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like)));
            }
            return p;
        };
    }

    public static Specification<Produto> produtoFilter(Long empresaId, Long grupoId, Long categoriaId, String tipo, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (grupoId != null) p = cb.and(p, cb.equal(root.get("grupo").get("id"), grupoId));
            if (categoriaId != null) p = cb.and(p, cb.equal(root.get("categoria").get("id"), categoriaId));
            if (tipo != null && !tipo.isBlank()) p = cb.and(p, cb.equal(root.get("tipo"), tipo));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like),
                        cb.like(cb.lower(root.get("codigoBarras")), like)));
            }
            return p;
        };
    }

    public static Specification<TabelaPreco> tabelaPrecoFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("nome")), like)));
            }
            return p;
        };
    }

    public static Specification<Transportadora> transportadoraFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("codigo")), like),
                        cb.like(cb.lower(root.get("razaoSocial")), like),
                        cb.like(cb.lower(root.get("cpfCnpj")), like)));
            }
            return p;
        };
    }

    public static Specification<ContaBancaria> contaBancariaFilter(Long empresaId, String term) {
        return (root, query, cb) -> {
            var p = cb.equal(root.get("deleted"), false);
            if (empresaId != null) p = cb.and(p, cb.equal(root.get("empresaId"), empresaId));
            if (term != null && !term.isBlank()) {
                var like = "%" + term.toLowerCase() + "%";
                p = cb.and(p, cb.or(
                        cb.like(cb.lower(root.get("agencia")), like),
                        cb.like(cb.lower(root.get("conta")), like),
                        cb.like(cb.lower(root.get("descricao")), like)));
            }
            return p;
        };
    }
}
