package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Categoria;
import com.orionerp.modules.cadastros.domain.GrupoProduto;
import com.orionerp.modules.cadastros.domain.Marca;
import com.orionerp.modules.cadastros.domain.Produto;
import com.orionerp.modules.cadastros.domain.SubgrupoProduto;
import com.orionerp.modules.cadastros.domain.UnidadeMedida;
import com.orionerp.modules.cadastros.dto.ProdutoRequest;
import com.orionerp.modules.cadastros.dto.ProdutoResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.CategoriaRepository;
import com.orionerp.modules.cadastros.repository.GrupoProdutoRepository;
import com.orionerp.modules.cadastros.repository.MarcaRepository;
import com.orionerp.modules.cadastros.repository.ProdutoRepository;
import com.orionerp.modules.cadastros.repository.SubgrupoProdutoRepository;
import com.orionerp.modules.cadastros.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final GrupoProdutoRepository grupoProdutoRepository;
    private final SubgrupoProdutoRepository subgrupoProdutoRepository;
    private final MarcaRepository marcaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> list(Long empresaId, Long grupoId, Long categoriaId, String tipo, String term, Pageable pageable) {
        return produtoRepository.findAll(
                CadastrosSpecifications.produtoFilter(empresaId, grupoId, categoriaId, tipo, term), pageable
        ).map(ProdutoResponse::from);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse getById(Long id) {
        Produto entity = produtoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        return ProdutoResponse.from(entity);
    }

    @Transactional
    public ProdutoResponse create(ProdutoRequest request) {
        if (produtoRepository.existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe um produto com o código: " + request.codigo());
        }

        Produto entity = new Produto();
        mapFields(entity, request);

        entity = produtoRepository.save(entity);
        return ProdutoResponse.from(entity);
    }

    @Transactional
    public ProdutoResponse update(Long id, ProdutoRequest request) {
        Produto entity = produtoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));

        if (produtoRepository.existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalseAndIdNot(request.empresaId(), request.codigo(), id)) {
            throw new BusinessException("Já existe outro produto com o código: " + request.codigo());
        }

        mapFields(entity, request);

        entity = produtoRepository.save(entity);
        return ProdutoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Produto entity = produtoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
        entity.softDelete();
        produtoRepository.save(entity);
    }

    private void mapFields(Produto entity, ProdutoRequest request) {
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setDescricao(request.descricao());

        if (request.grupoId() != null) {
            GrupoProduto grupo = grupoProdutoRepository.findByIdAndDeletedFalse(request.grupoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grupo de produto não encontrado: " + request.grupoId()));
            entity.setGrupo(grupo);
        } else {
            entity.setGrupo(null);
        }

        if (request.subgrupoId() != null) {
            SubgrupoProduto subgrupo = subgrupoProdutoRepository.findByIdAndDeletedFalse(request.subgrupoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subgrupo de produto não encontrado: " + request.subgrupoId()));
            entity.setSubgrupo(subgrupo);
        } else {
            entity.setSubgrupo(null);
        }

        if (request.marcaId() != null) {
            Marca marca = marcaRepository.findByIdAndDeletedFalse(request.marcaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada: " + request.marcaId()));
            entity.setMarca(marca);
        } else {
            entity.setMarca(null);
        }

        if (request.categoriaId() != null) {
            Categoria categoria = categoriaRepository.findByIdAndDeletedFalse(request.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + request.categoriaId()));
            entity.setCategoria(categoria);
        } else {
            entity.setCategoria(null);
        }

        if (request.unidadeMedidaId() != null) {
            UnidadeMedida unidade = unidadeMedidaRepository.findByIdAndAtivoTrue(request.unidadeMedidaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidade de medida não encontrada: " + request.unidadeMedidaId()));
            entity.setUnidadeMedida(unidade);
        } else {
            entity.setUnidadeMedida(null);
        }

        entity.setCodigoBarras(request.codigoBarras());
        entity.setNcm(request.ncm());
        entity.setPesoBruto(request.pesoBruto());
        entity.setPesoLiquido(request.pesoLiquido());
        if (request.precoCusto() != null) entity.setPrecoCusto(request.precoCusto());
        if (request.precoVenda() != null) entity.setPrecoVenda(request.precoVenda());
        if (request.estoqueMinimo() != null) entity.setEstoqueMinimo(request.estoqueMinimo());
        if (request.estoqueMaximo() != null) entity.setEstoqueMaximo(request.estoqueMaximo());
        if (request.controlaEstoque() != null) entity.setControlaEstoque(request.controlaEstoque());
        if (request.controlaLote() != null) entity.setControlaLote(request.controlaLote());
        if (request.controlaValidade() != null) entity.setControlaValidade(request.controlaValidade());
        if (request.tipo() != null) entity.setTipo(request.tipo());
        entity.setObservacao(request.observacao());
    }
}
