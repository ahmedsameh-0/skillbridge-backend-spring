package com.skillbridge.entity;

import com.skillbridge.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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
