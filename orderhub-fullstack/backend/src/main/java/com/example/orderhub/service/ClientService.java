package com.example.orderhub.service;

import com.example.orderhub.dto.ClientRequest;
import com.example.orderhub.entity.Client;
import com.example.orderhub.exception.ResourceNotFoundException;
import com.example.orderhub.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    public Client create(ClientRequest request) {
        Client client = Client.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .document(request.document())
                .active(request.active() == null ? true : request.active())
                .build();
        return clientRepository.save(client);
    }

    public Client update(Long id, ClientRequest request) {
        Client client = findById(id);
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setDocument(request.document());
        client.setActive(request.active() == null ? client.getActive() : request.active());
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        Client client = findById(id);
        client.setActive(false);
        clientRepository.save(client);
    }
}
