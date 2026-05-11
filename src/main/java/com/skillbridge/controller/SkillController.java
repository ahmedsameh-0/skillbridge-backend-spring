package com.skillbridge.controller;

import com.skillbridge.dto.SkillDTO;
import com.skillbridge.service.impl.SkillServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillServiceImpl skillServiceImpl;

    @GetMapping
    public Flux<SkillDTO> getAllSkills() {
        return skillServiceImpl.getAllSkills();
    }

    @GetMapping("/search")
    public Flux<SkillDTO> searchSkills(@RequestParam String q) {
        return skillServiceImpl.searchSkills(q);
    }

    @GetMapping("/filter")
    public Flux<SkillDTO> filterByCategory(@RequestParam String category) {
        return skillServiceImpl.filterByCategory(category);
    }
}
