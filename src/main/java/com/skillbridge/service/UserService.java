package com.skillbridge.service;

import com.skillbridge.client.ApiClient;
import com.skillbridge.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ApiClient apiClient;

    /**
     * Get all users
     */
    public Flux<UserDTO> getAllUsers() {
        return apiClient.getAllUsers();
    }

    /**
     * Get user by ID
     */
    public Mono<UserDTO> getUserById(Long id) {
        return apiClient.getUserById(id);
    }
}
