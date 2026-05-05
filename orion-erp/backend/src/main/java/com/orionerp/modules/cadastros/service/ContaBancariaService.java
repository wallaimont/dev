package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Banco;
import com.orionerp.modules.cadastros.domain.ContaBancaria;
import com.orionerp.modules.cadastros.dto.ContaBancariaRequest;
import com.orionerp.modules.cadastros.dto.ContaBancariaResponse;
import com.orionerp.modules.cadastros.repository.BancoRepository;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.ContaBancariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContaBancariaService {

    private final ContaBancariaRepository contaBancariaRepository;
    private final BancoRepository bancoRepository;

    @Transactional(readOnly = true)
    public Page<ContaBancariaResponse> list(Long empresaId, String term, Pageable pageable) {
        return contaBancariaRepository.findAll(
                CadastrosSpecifications.contaBancariaFilter(empresaId, term), pageable
        ).map(ContaBancariaResponse::from);
    }

    @Transactional(readOnly = true)
    public ContaBancariaResponse getById(Long id) {
        ContaBancaria entity = contaBancariaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada: " + id));
        return ContaBancariaResponse.from(entity);
    }

    @Transactional
    public ContaBancariaResponse create(ContaBancariaRequest request) {
        Banco banco = bancoRepository.findByIdAndAtivoTrue(request.bancoId())
                .orElseThrow(() -> new ResourceNotFoundException("Banco não encontrado: " + request.bancoId()));

        ContaBancaria entity = new ContaBancaria();
        entity.setEmpresaId(request.empresaId());
        entity.setFilialId(request.filialId());
        entity.setBanco(banco);
        entity.setAgencia(request.agencia());
        entity.setConta(request.conta());
        entity.setDigito(request.digito());
        if (request.tipo() != null) entity.setTipo(request.tipo());
        entity.setDescricao(request.descricao());
        if (request.saldoInicial() != null) entity.setSaldoInicial(request.saldoInicial());

        entity = contaBancariaRepository.save(entity);
        return ContaBancariaResponse.from(entity);
    }

    @Transactional
    public ContaBancariaResponse update(Long id, ContaBancariaRequest request) {
        ContaBancaria entity = contaBancariaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada: " + id));

        Banco banco = bancoRepository.findByIdAndAtivoTrue(request.bancoId())
                .orElseThrow(() -> new ResourceNotFoundException("Banco não encontrado: " + request.bancoId()));

        entity.setFilialId(request.filialId());
        entity.setBanco(banco);
        entity.setAgencia(request.agencia());
        entity.setConta(request.conta());
        entity.setDigito(request.digito());
        if (request.tipo() != null) entity.setTipo(request.tipo());
        entity.setDescricao(request.descricao());
        if (request.saldoInicial() != null) entity.setSaldoInicial(request.saldoInicial());

        entity = contaBancariaRepository.save(entity);
        return ContaBancariaResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        ContaBancaria entity = contaBancariaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada: " + id));
        entity.softDelete();
        contaBancariaRepository.save(entity);
    }
}
