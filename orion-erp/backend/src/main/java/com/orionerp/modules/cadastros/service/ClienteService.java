package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Cliente;
import com.orionerp.modules.cadastros.dto.ClienteRequest;
import com.orionerp.modules.cadastros.dto.ClienteResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public Page<ClienteResponse> list(Long empresaId, String term, Pageable pageable) {
        return clienteRepository.findAll(
                CadastrosSpecifications.clienteFilter(empresaId, term), pageable
        ).map(ClienteResponse::from);
    }

    @Transactional(readOnly = true)
    public ClienteResponse getById(Long id) {
        Cliente entity = clienteRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
        return ClienteResponse.from(entity);
    }

    @Transactional
    public ClienteResponse create(ClienteRequest request) {
        Cliente entity = new Cliente();
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
        entity.setCelular(request.celular());
        entity.setEmail(request.email());
        entity.setWebsite(request.website());
        if (request.limiteCredito() != null) entity.setLimiteCredito(request.limiteCredito());
        if (request.bloqueioFinanceiro() != null) entity.setBloqueioFinanceiro(request.bloqueioFinanceiro());
        if (request.bloqueioComercial() != null) entity.setBloqueioComercial(request.bloqueioComercial());
        entity.setObservacao(request.observacao());

        entity = clienteRepository.save(entity);
        return ClienteResponse.from(entity);
    }

    @Transactional
    public ClienteResponse update(Long id, ClienteRequest request) {
        Cliente entity = clienteRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));

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
        entity.setCelular(request.celular());
        entity.setEmail(request.email());
        entity.setWebsite(request.website());
        if (request.limiteCredito() != null) entity.setLimiteCredito(request.limiteCredito());
        if (request.bloqueioFinanceiro() != null) entity.setBloqueioFinanceiro(request.bloqueioFinanceiro());
        if (request.bloqueioComercial() != null) entity.setBloqueioComercial(request.bloqueioComercial());
        entity.setObservacao(request.observacao());

        entity = clienteRepository.save(entity);
        return ClienteResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Cliente entity = clienteRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
        entity.softDelete();
        clienteRepository.save(entity);
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
