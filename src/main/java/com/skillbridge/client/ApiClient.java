package com.skillbridge.client;

import com.skillbridge.dto.ProfileDTO;
import com.skillbridge.dto.SkillDTO;
import com.skillbridge.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ApiClient {

    private final WebClient webClient;

    public Flux<SkillDTO> getAllSkills() {
        return webClient.get().uri("/api/skills").retrieve().bodyToFlux(SkillDTO.class);
    }

    public Flux<UserDTO> getAllUsers() {
        return webClient.get().uri("/api/users").retrieve().bodyToFlux(UserDTO.class);
    }

    public Mono<UserDTO> getUserById(Long id) {
        return webClient.get().uri("/api/users/{id}", id).retrieve().bodyToMono(UserDTO.class);
    }

    public Mono<ProfileDTO> updateProfile(ProfileDTO profileDTO) {
        return webClient.post().uri("/api/settings/profile").bodyValue(profileDTO).retrieve().bodyToMono(ProfileDTO.class);
    }

    public Mono<?> updatePassword(String oldPassword, String newPassword) {
        return webClient.put().uri("/api/settings/password")
                .bodyValue(new java.util.HashMap<String, String>() {{
                    put("oldPassword", oldPassword);
                    put("newPassword", newPassword);
                }})
                .retrieve().bodyToMono(Void.class);
    }
}
