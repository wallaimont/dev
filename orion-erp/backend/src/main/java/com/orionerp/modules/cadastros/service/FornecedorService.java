package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Fornecedor;
import com.orionerp.modules.cadastros.dto.FornecedorRequest;
import com.orionerp.modules.cadastros.dto.FornecedorResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    @Transactional(readOnly = true)
    public Page<FornecedorResponse> list(Long empresaId, String term, Pageable pageable) {
        return fornecedorRepository.findAll(
                CadastrosSpecifications.fornecedorFilter(empresaId, term), pageable
        ).map(FornecedorResponse::from);
    }

    @Transactional(readOnly = true)
    public FornecedorResponse getById(Long id) {
        Fornecedor entity = fornecedorRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado: " + id));
        return FornecedorResponse.from(entity);
    }

    @Transactional
    public FornecedorResponse create(FornecedorRequest request) {
        Fornecedor entity = new Fornecedor();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setTipoPessoa(normalizeTipoPessoa(request.tipoPessoa()));
        entity.setRazaoSocial(request.razaoSocial());
        entity.setNomeFantasia(request.nomeFantasia());
        entity.setCpfCnpj(request.cpfCnpj());
        entity.setInscricaoEstadual(request.inscricaoEstadual());
        entity.setEndereco(request.endereco());
        entity.setNumero(request.numero());
        entity.setComplemento(request.complemento());
        entity.setBairro(request.bairro());
        entity.setCidade(request.cidade());
        entity.setUf(request.uf());
        entity.setCep(request.cep());
        entity.setTelefone(request.telefone());
        entity.setEmail(request.email());
        entity.setWebsite(request.website());
        entity.setObservacao(request.observacao());

        entity = fornecedorRepository.save(entity);
        return FornecedorResponse.from(entity);
    }

    @Transactional
    public FornecedorResponse update(Long id, FornecedorRequest request) {
        Fornecedor entity = fornecedorRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado: " + id));

        entity.setCodigo(request.codigo());
        if (request.tipoPessoa() != null) entity.setTipoPessoa(normalizeTipoPessoa(request.tipoPessoa()));
        entity.setRazaoSocial(request.razaoSocial());
        entity.setNomeFantasia(request.nomeFantasia());
        entity.setCpfCnpj(request.cpfCnpj());
        entity.setInscricaoEstadual(request.inscricaoEstadual());
        entity.setEndereco(request.endereco());
        entity.setNumero(request.numero());
        entity.setComplemento(request.complemento());
        entity.setBairro(request.bairro());
        entity.setCidade(request.cidade());
        entity.setUf(request.uf());
        entity.setCep(request.cep());
        entity.setTelefone(request.telefone());
        entity.setEmail(request.email());
        entity.setWebsite(request.website());
        entity.setObservacao(request.observacao());

        entity = fornecedorRepository.save(entity);
        return FornecedorResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Fornecedor entity = fornecedorRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado: " + id));
        entity.softDelete();
        fornecedorRepository.save(entity);
    }

    private String normalizeTipoPessoa(String tipo) {
        if (tipo == null) return "J";
        return switch (tipo.toUpperCase()) {
            case "FISICA", "F" -> "F";
            case "JURIDICA", "J" -> "J";
            default -> "J";
        };
    }
}
