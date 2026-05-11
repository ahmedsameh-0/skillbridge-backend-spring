package com.skillbridge.controller;

import com.skillbridge.dto.MatchDTO;
import com.skillbridge.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/matches")
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public Flux<MatchDTO> getAllMatches(@RequestParam Long userId) {
        return matchService.getAllMatches(userId);
    }

    @GetMapping("/suggested")
    public Flux<MatchDTO> getSuggestedMatches(@RequestParam Long userId) {
        return matchService.getSuggestedMatches(userId);
    }

    @GetMapping("/{id}")
    public Mono<MatchDTO> getMatchDetails(@PathVariable Long id) {
        return matchService.getMatchDetails(id);
    }

    @PostMapping
    public Mono<MatchDTO> createMatch(@Valid @RequestBody MatchDTO matchDTO) {
        return matchService.createMatch(matchDTO);
    }

    @PatchMapping("/{id}/accept")
    public Mono<MatchDTO> acceptMatch(@PathVariable Long id) {
        return matchService.acceptMatch(id);
    }

    @PatchMapping("/{id}/reject")
    public Mono<MatchDTO> rejectMatch(@PathVariable Long id) {
        return matchService.rejectMatch(id);
    }
}
