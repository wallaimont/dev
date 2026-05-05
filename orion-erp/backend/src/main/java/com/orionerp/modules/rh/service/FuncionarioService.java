package com.orionerp.modules.rh.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.Funcionario;
import com.orionerp.modules.rh.dto.FuncionarioRequest;
import com.orionerp.modules.rh.dto.FuncionarioResponse;
import com.orionerp.modules.rh.repository.FuncionarioRepository;
import com.orionerp.modules.rh.repository.RhSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    @Transactional(readOnly = true)
    public PageResponse<FuncionarioResponse> list(Long empresaId, Long filialId, Long departamentoId,
                                                   String situacao, String term, Pageable pageable) {
        var spec = RhSpecifications.funcionarioFilter(empresaId, filialId, departamentoId, situacao, term);
        var page = funcionarioRepository.findAll(spec, pageable).map(FuncionarioResponse::from);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public FuncionarioResponse getById(Long id) {
        return FuncionarioResponse.from(findActiveById(id));
    }

    @Transactional
    public FuncionarioResponse create(FuncionarioRequest request) {
        if (funcionarioRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(
                request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe funcionário com código '" + request.codigo() + "'");
        }
        if (funcionarioRepository.existsByEmpresaIdAndCpfAndDeletedFalse(
                request.empresaId(), request.cpf())) {
            throw new BusinessException("Já existe funcionário com CPF '" + request.cpf() + "'");
        }

        Funcionario f = new Funcionario();
        f.setEmpresaId(request.empresaId());
        f.setFilialId(request.filialId());
        f.setCodigo(request.codigo());
        f.setNome(request.nome());
        f.setCpf(request.cpf());
        f.setRg(request.rg());
        f.setDataNascimento(request.dataNascimento());
        f.setSexo(normalizeSexo(request.sexo()));
        f.setEstadoCivil(request.estadoCivil());
        f.setEndereco(request.endereco());
        f.setCidade(request.cidade());
        f.setUf(request.uf());
        f.setCep(request.cep());
        f.setTelefone(request.telefone());
        f.setEmail(request.email());
        f.setDepartamentoId(request.departamentoId());
        f.setCargoId(request.cargoId());
        f.setDataAdmissao(request.dataAdmissao());
        f.setSalario(request.salario());
        f.setPis(request.pis());
        f.setCtps(request.ctps());
        f.setObservacao(request.observacao());
        f.setSituacao("ATIVO");

        return FuncionarioResponse.from(funcionarioRepository.save(f));
    }

    @Transactional
    public FuncionarioResponse demitir(Long id, LocalDate dataDemissao) {
        Funcionario f = findActiveById(id);

        if ("DEMITIDO".equals(f.getSituacao())) {
            throw new BusinessException("Funcionário já está demitido");
        }

        final LocalDate dataEfetiva = dataDemissao != null ? dataDemissao : LocalDate.now();
        if (dataEfetiva.isBefore(f.getDataAdmissao())) {
            throw new BusinessException("Data de demissão não pode ser anterior à data de admissão");
        }

        f.setSituacao("DEMITIDO");
        f.setDataDemissao(dataEfetiva);
        f.setAtivo(false);
        return FuncionarioResponse.from(funcionarioRepository.save(f));
    }

    @Transactional
    public FuncionarioResponse alterarSituacao(Long id, String novaSituacao) {
        Funcionario f = findActiveById(id);

        if ("DEMITIDO".equals(f.getSituacao())) {
            throw new BusinessException("Não é possível alterar situação de funcionário demitido");
        }

        f.setSituacao(novaSituacao);
        return FuncionarioResponse.from(funcionarioRepository.save(f));
    }

    private Funcionario findActiveById(Long id) {
        return funcionarioRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado: " + id));
    }

    private String normalizeSexo(String sexo) {
        if (sexo == null) return null;
        return switch (sexo.toUpperCase().trim()) {
            case "MASCULINO", "M" -> "M";
            case "FEMININO", "F" -> "F";
            default -> sexo.length() > 1 ? sexo.substring(0, 1).toUpperCase() : sexo.toUpperCase();
        };
    }
}
