package com.skillbridge.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationDTO {
    private Long id;
    private String type;
    private Long lastMessageId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long otherUserId;
    private String otherUserName;
    private LocalDateTime createdAt;
}
