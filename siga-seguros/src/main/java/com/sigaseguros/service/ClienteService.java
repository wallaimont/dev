package com.sigaseguros.service;

import com.sigaseguros.dto.ClienteDTO;
import com.sigaseguros.entity.Cliente;
import com.sigaseguros.enums.TipoPessoa;
import com.sigaseguros.exception.BusinessException;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<ClienteDTO> listar(String nome, String cpfCnpj, TipoPessoa tipoPessoa, String email, Pageable pageable) {
        return clienteRepository.findAllWithFilters(nome, cpfCnpj, tipoPessoa, email, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public ClienteDTO criar(ClienteDTO dto) {
        validarDocumentos(dto, null);
        Cliente cliente = toEntity(dto, new Cliente());
        cliente.setActive(true);
        cliente = clienteRepository.save(cliente);
        auditoriaService.registrar("Cliente", cliente.getId(), "CRIAR");
        return toDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO dto) {
        Cliente cliente = findById(id);
        validarDocumentos(dto, id);
        cliente = toEntity(dto, cliente);
        cliente = clienteRepository.save(cliente);
        auditoriaService.registrar("Cliente", cliente.getId(), "ATUALIZAR");
        return toDTO(cliente);
    }

    @Transactional
    public void inativar(Long id) {
        Cliente cliente = findById(id);
        cliente.setActive(false);
        clienteRepository.save(cliente);
        auditoriaService.registrar("Cliente", id, "INATIVAR");
    }

    private void validarDocumentos(ClienteDTO dto, Long idExcluir) {
        if (dto.getTipoPessoa() == TipoPessoa.PF && dto.getCpf() != null) {
            boolean existe = idExcluir == null
                    ? clienteRepository.existsByCpfAndActiveTrue(dto.getCpf())
                    : clienteRepository.existsByCpfAndIdNotAndActiveTrue(dto.getCpf(), idExcluir);
            if (existe) throw new BusinessException("CPF já cadastrado");
        }
        if (dto.getTipoPessoa() == TipoPessoa.PJ && dto.getCnpj() != null) {
            boolean existe = idExcluir == null
                    ? clienteRepository.existsByCnpjAndActiveTrue(dto.getCnpj())
                    : clienteRepository.existsByCnpjAndIdNotAndActiveTrue(dto.getCnpj(), idExcluir);
            if (existe) throw new BusinessException("CNPJ já cadastrado");
        }
    }

    private Cliente findById(Long id) {
        return clienteRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    private ClienteDTO toDTO(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId());
        dto.setTipoPessoa(c.getTipoPessoa());
        dto.setNome(c.getNome());
        dto.setRazaoSocial(c.getRazaoSocial());
        dto.setNomeFantasia(c.getNomeFantasia());
        dto.setCpf(c.getCpf());
        dto.setCnpj(c.getCnpj());
        dto.setRg(c.getRg());
        dto.setInscricaoEstadual(c.getInscricaoEstadual());
        dto.setDataNascimento(c.getDataNascimento() != null ? c.getDataNascimento().format(FMT_DATE) : null);
        dto.setDataFundacao(c.getDataFundacao() != null ? c.getDataFundacao().format(FMT_DATE) : null);
        dto.setTelefone(c.getTelefone());
        dto.setCelular(c.getCelular());
        dto.setEmail(c.getEmail());
        dto.setCep(c.getCep());
        dto.setLogradouro(c.getLogradouro());
        dto.setNumero(c.getNumero());
        dto.setComplemento(c.getComplemento());
        dto.setBairro(c.getBairro());
        dto.setCidade(c.getCidade());
        dto.setEstado(c.getEstado());
        dto.setObservacoes(c.getObservacoes());
        dto.setActive(c.getActive());
        dto.setNomeExibicao(c.getNomeExibicao());
        dto.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().format(FMT_DT) : null);
        return dto;
    }

    private Cliente toEntity(ClienteDTO dto, Cliente c) {
        c.setTipoPessoa(dto.getTipoPessoa());
        c.setNome(dto.getNome());
        c.setRazaoSocial(dto.getRazaoSocial());
        c.setNomeFantasia(dto.getNomeFantasia());
        c.setCpf(dto.getCpf());
        c.setCnpj(dto.getCnpj());
        c.setRg(dto.getRg());
        c.setInscricaoEstadual(dto.getInscricaoEstadual());
        c.setDataNascimento(dto.getDataNascimento() != null ? LocalDate.parse(dto.getDataNascimento(), FMT_DATE) : null);
        c.setDataFundacao(dto.getDataFundacao() != null ? LocalDate.parse(dto.getDataFundacao(), FMT_DATE) : null);
        c.setTelefone(dto.getTelefone());
        c.setCelular(dto.getCelular());
        c.setEmail(dto.getEmail());
        c.setCep(dto.getCep());
        c.setLogradouro(dto.getLogradouro());
        c.setNumero(dto.getNumero());
        c.setComplemento(dto.getComplemento());
        c.setBairro(dto.getBairro());
        c.setCidade(dto.getCidade());
        c.setEstado(dto.getEstado());
        c.setObservacoes(dto.getObservacoes());
        return c;
    }
}
