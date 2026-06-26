package com.skillbridge.session.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long mentorId;
    private Long skillId;
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
