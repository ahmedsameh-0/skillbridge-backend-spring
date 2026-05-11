package com.skillbridge.controller;

import com.skillbridge.dto.SessionDTO;
import com.skillbridge.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/pending")
    public Flux<SessionDTO> getPendingSessions() {
        return sessionService.getSessionsByStatus("PENDING");
    }

    @GetMapping("/active")
    public Flux<SessionDTO> getActiveSessions() {
        return sessionService.getSessionsByStatus("ACTIVE");
    }

    @GetMapping("/completed")
    public Flux<SessionDTO> getCompletedSessions() {
        return sessionService.getSessionsByStatus("COMPLETED");
    }

    @GetMapping("/{id}")
    public Mono<SessionDTO> getSessionDetails(@PathVariable Long id) {
        return sessionService.getSessionDetails(id);
    }

    @PostMapping
    public Mono<SessionDTO> createSession(@Valid @RequestBody SessionDTO sessionDTO) {
        return sessionService.createSession(sessionDTO);
    }

    @PatchMapping("/{id}/accept")
    public Mono<SessionDTO> acceptSession(@PathVariable Long id) {
        return sessionService.acceptSession(id);
    }

    @PatchMapping("/{id}/close")
    public Mono<SessionDTO> closeSession(@PathVariable Long id) {
        return sessionService.closeSession(id);
    }
}
