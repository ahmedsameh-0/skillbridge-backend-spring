package com.skillbridge.service;

import com.skillbridge.client.ApiClient;
import com.skillbridge.dto.ProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SettingsService {

    private final ApiClient apiClient;

    public Mono<ProfileDTO> updateProfile(ProfileDTO profileDTO) {
        return apiClient.updateProfile(profileDTO);
    }

    public Mono<?> updatePassword(String oldPassword, String newPassword) {
        return apiClient.updatePassword(oldPassword, newPassword);
    }
}
