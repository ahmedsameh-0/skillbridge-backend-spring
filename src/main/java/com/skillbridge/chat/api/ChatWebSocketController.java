package com.skillbridge.chat.api;

import com.skillbridge.chat.dto.MessageDTO;
import com.skillbridge.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat/{sessionId}")
    @SendTo("/topic/messages/{sessionId}")
    public Mono<MessageDTO> sendMessage(@DestinationVariable Long sessionId, @Valid MessageDTO message) {
        message.setSessionId(sessionId);
        return chatService.saveMessage(message);
    }
}
