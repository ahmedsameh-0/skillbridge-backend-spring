package com.skillbridge.service;

import com.skillbridge.dto.SessionDTO;
import com.skillbridge.entity.Session;
import com.skillbridge.enums.SessionStatus;
import com.skillbridge.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final NotificationService notificationService;

    public Flux<SessionDTO> getSessionsByStatus(String status) {
        SessionStatus sessionStatus = parseSessionStatus(status);
        return Flux.defer(() -> Flux.fromIterable(sessionRepository.findByStatus(sessionStatus)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    public Mono<SessionDTO> getSessionDetails(Long id) {
        return Mono.fromCallable(() -> sessionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> Mono.justOrEmpty(optional.map(this::convertToDTO)));
    }

    public Mono<SessionDTO> createSession(SessionDTO sessionDTO) {
        return Mono.fromCallable(() -> {
                    Session session = new Session();
                    session.setUserId(sessionDTO.getUserId());
                    session.setMentorId(sessionDTO.getMentorId());
                    session.setSkillId(sessionDTO.getSkillId());
                    session.setStatus(parseSessionStatus(sessionDTO.getStatus()));
                    session.setStartTime(sessionDTO.getStartTime());
                    session.setEndTime(sessionDTO.getEndTime());
                    return sessionRepository.save(session);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    public Mono<SessionDTO> acceptSession(Long id) {
        return Mono.fromCallable(() -> sessionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> {
                    if (optional.isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found with id: " + id));
                    }
                    Session session = optional.get();
                    if (session.getStatus() != SessionStatus.PENDING) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending sessions can be accepted."));
                    }
                    session.setStatus(SessionStatus.ACTIVE);
                    if (session.getStartTime() == null) {
                        session.setStartTime(LocalDateTime.now());
                    }
                    return Mono.fromCallable(() -> sessionRepository.save(session))
                            .subscribeOn(Schedulers.boundedElastic())
                            // Notify frontend that the session is accepted and chat should open.
                            .doOnSuccess(savedSession -> notificationService.sendSessionUpdate(savedSession.getId(), "SESSION_ACCEPTED", "Session accepted and ready for chat"))
                            .map(this::convertToDTO);
                });
    }

    public Mono<SessionDTO> closeSession(Long id) {
        return Mono.fromCallable(() -> sessionRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> {
                    if (optional.isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found with id: " + id));
                    }
                    Session session = optional.get();
                    if (session.getStatus() != SessionStatus.ACTIVE) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only active sessions can be closed."));
                    }
                    session.setStatus(SessionStatus.COMPLETED);
                    session.setEndTime(LocalDateTime.now());
                    return Mono.fromCallable(() -> sessionRepository.save(session))
                            .subscribeOn(Schedulers.boundedElastic())
                            // Notify frontend that the session is closed and chat should end.
                            .doOnSuccess(savedSession -> notificationService.sendSessionUpdate(savedSession.getId(), "SESSION_CLOSED", "Session closed and chat ended"))
                            .map(this::convertToDTO);
                });
    }

    private SessionStatus parseSessionStatus(String status) {
        if (status == null || status.isBlank()) {
            return SessionStatus.PENDING;
        }
        try {
            return SessionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid session status: " + status);
        }
    }

    private SessionDTO convertToDTO(Session session) {
        SessionDTO dto = new SessionDTO();
        dto.setId(session.getId());
        dto.setUserId(session.getUserId());
        dto.setMentorId(session.getMentorId());
        dto.setSkillId(session.getSkillId());
        dto.setStatus(session.getStatus() != null ? session.getStatus().name() : null);
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        return dto;
    }
}
