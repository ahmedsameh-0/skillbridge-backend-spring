package com.skillbridge.chat.api;

import com.skillbridge.chat.dto.ConversationDTO;
import com.skillbridge.chat.dto.MessageDTO;
import com.skillbridge.chat.service.ChatService;
import com.skillbridge.chat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;
    private final ConversationService conversationService;

    @GetMapping("/conversations")
    public Flux<ConversationDTO> getConversations() {
        return conversationService.getAllConversations();
    }

    @GetMapping("/{sessionId}/messages")
    public Flux<MessageDTO> getMessages(@PathVariable Long sessionId) {
        return chatService.getMessagesBySession(sessionId);
    }
}

