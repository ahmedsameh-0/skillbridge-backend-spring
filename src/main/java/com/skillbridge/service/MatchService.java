package com.skillbridge.service;

import com.skillbridge.dto.MatchDTO;
import com.skillbridge.entity.Match;
import com.skillbridge.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final NotificationService notificationService;

    /**
     * Get all matches for a user
     */
    public Flux<MatchDTO> getAllMatches(Long userId) {
        return Flux.defer(() -> Flux.fromIterable(matchRepository.findByUserId(userId)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    /**
     * Get suggested matches (PENDING status for the user)
     */
    public Flux<MatchDTO> getSuggestedMatches(Long userId) {
        return Flux.defer(() -> Flux.fromIterable(matchRepository.findByUserIdAndStatus(userId, "PENDING")))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    /**
     * Get match details by match ID
     */
    public Mono<MatchDTO> getMatchDetails(Long matchId) {
        return Mono.fromCallable(() -> matchRepository.findById(matchId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> {
                    if (optional.isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found with id: " + matchId));
                    }
                    return Mono.just(convertToDTO(optional.get()));
                });
    }

    /**
     * Create a new match suggestion
     */
    public Mono<MatchDTO> createMatch(MatchDTO matchDTO) {
        return Mono.fromCallable(() -> {
                    Match match = new Match();
                    match.setUserId(matchDTO.getUserId());
                    match.setMatchedUserId(matchDTO.getMatchedUserId());
                    match.setSkillId(matchDTO.getSkillId());
                    match.setMatchScore(matchDTO.getMatchScore());
                    match.setStatus("PENDING");
                    return matchRepository.save(match);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(savedMatch -> notificationService.sendMatchNotification(savedMatch.getId(), "NEW_MATCH", "New match suggestion available"))
                .map(this::convertToDTO);
    }

    /**
     * Accept a match and create a session (optionally)
     */
    public Mono<MatchDTO> acceptMatch(Long matchId) {
        return Mono.fromCallable(() -> matchRepository.findById(matchId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> {
                    if (optional.isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found with id: " + matchId));
                    }
                    Match match = optional.get();
                    if (!"PENDING".equals(match.getStatus())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending matches can be accepted."));
                    }
                    match.setStatus("ACCEPTED");
                    return Mono.fromCallable(() -> matchRepository.save(match))
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnSuccess(savedMatch -> notificationService.sendMatchNotification(savedMatch.getId(), "MATCH_ACCEPTED", "Match accepted successfully"))
                            .map(this::convertToDTO);
                });
    }

    /**
     * Reject a match
     */
    public Mono<MatchDTO> rejectMatch(Long matchId) {
        return Mono.fromCallable(() -> matchRepository.findById(matchId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> {
                    if (optional.isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found with id: " + matchId));
                    }
                    Match match = optional.get();
                    if (!"PENDING".equals(match.getStatus())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending matches can be rejected."));
                    }
                    match.setStatus("REJECTED");
                    return Mono.fromCallable(() -> matchRepository.save(match))
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnSuccess(savedMatch -> notificationService.sendMatchNotification(savedMatch.getId(), "MATCH_REJECTED", "Match rejected"))
                            .map(this::convertToDTO);
                });
    }

    private MatchDTO convertToDTO(Match match) {
        MatchDTO dto = new MatchDTO();
        dto.setId(match.getId());
        dto.setUserId(match.getUserId());
        dto.setMatchedUserId(match.getMatchedUserId());
        dto.setSkillId(match.getSkillId());
        dto.setMatchScore(match.getMatchScore());
        return dto;
    }
}
