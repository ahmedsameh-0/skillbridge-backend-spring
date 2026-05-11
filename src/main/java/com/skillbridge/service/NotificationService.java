package com.skillbridge.service;

import com.skillbridge.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendSessionUpdate(Long sessionId, String type, String message) {
        NotificationDTO notification = new NotificationDTO(type, message, sessionId);
        messagingTemplate.convertAndSend("/topic/sessions/" + sessionId, notification);
    }

    public void sendMatchNotification(Long matchId, String type, String message) {
        NotificationDTO notification = new NotificationDTO(type, message, matchId);
        messagingTemplate.convertAndSend("/topic/matches", notification);
    }
}
