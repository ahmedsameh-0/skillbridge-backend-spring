package com.skillbridge.session.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionDTO {
    private Long id;
    @NotNull(message = "User ID is required")
    private Long userId;
    @NotNull(message = "Mentor ID is required")
    private Long mentorId;
    @NotNull(message = "Skill ID is required")
    private Long skillId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
