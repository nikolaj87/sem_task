package com.example.securitytestproject.service;

import com.example.securitytestproject.dto.ClientRequestDto;
import com.example.securitytestproject.dto.ClientResponseDto;
import com.example.securitytestproject.entity.Client;
import com.example.securitytestproject.entity.Role;
import com.example.securitytestproject.mapper.ClientMapper;
import com.example.securitytestproject.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;


    @Override
    public ClientResponseDto getClientDtoById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDto)
                .orElseThrow();
    }

    @Override
    public ClientResponseDto createRequestDtoForManager(ClientRequestDto clientRequestDto) {
        Client client = clientMapper.toEntity(clientRequestDto);
        client.setRoles(Set.of(new Role(1L,"ADMIN")));
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    @Override
    public ClientResponseDto createRequestDtoForClient(ClientRequestDto clientRequestDto) {
        Client client = clientMapper.toEntity(clientRequestDto);
        client.setRoles(Set.of(new Role(1L,"USER")));
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    @Override
    public void deleteClientByName(String name) {
        Client client = clientRepository.findClientByName(name);
        if (client != null) {
            Set<Role> roles = client.getRoles();
            for (Role role : roles) {
                if ("ADMIN".equals(role.getName())) {
                    throw new UnsupportedOperationException
                            ("Невозможно удалить клиента, так как он является администратором.");
                }
            }
        }
        clientRepository.deleteClientByName(name);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Client> clientOptional = clientRepository.findClientByEmail(email);
        if (clientOptional.isEmpty()) {
            throw new UsernameNotFoundException("No such name");
        }
        return clientOptional.get();
    }
}