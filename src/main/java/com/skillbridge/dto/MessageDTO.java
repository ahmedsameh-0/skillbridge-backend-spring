package com.skillbridge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private LocalDateTime timestamp;
}
