package com.skillbridge.service.impl;

import com.skillbridge.client.ApiClient;
import com.skillbridge.dto.SkillDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl {

    private final ApiClient apiClient;

    public Flux<SkillDTO> getAllSkills() {
        return apiClient.getAllSkills();
    }

    public Flux<SkillDTO> searchSkills(String query) {
        return apiClient.getAllSkills()
                .filter(skill -> skill.getName().toLowerCase().contains(query.toLowerCase()) ||
                        skill.getDescription().toLowerCase().contains(query.toLowerCase()));
    }

    public Flux<SkillDTO> filterByCategory(String category) {
        return apiClient.getAllSkills()
                .filter(skill -> skill.getCategory().equalsIgnoreCase(category));
    }
}
