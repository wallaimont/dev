package com.empresa.sgc.service;

import com.empresa.sgc.dto.ClienteRequest;
import com.empresa.sgc.entity.Cliente;
import com.empresa.sgc.exception.BusinessException;
import com.empresa.sgc.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Cliente criar(ClienteRequest request) {
        if (request.getDocumento() != null && !request.getDocumento().isBlank() && clienteRepository.existsByDocumento(request.getDocumento())) {
            throw new BusinessException("Documento já cadastrado");
        }

        Cliente cliente = Cliente.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .telefone(request.getTelefone())
                .documento(request.getDocumento())
                .endereco(request.getEndereco())
                .build();
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listar(String nome) {
        if (nome != null && !nome.isBlank()) {
            return clienteRepository.findByNomeContainingIgnoreCase(nome);
        }
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }

    public Cliente atualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setDocumento(request.getDocumento());
        cliente.setEndereco(request.getEndereco());
        return clienteRepository.save(cliente);
    }

    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }
}
