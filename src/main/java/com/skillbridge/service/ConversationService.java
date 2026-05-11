package com.skillbridge.service;

import com.skillbridge.dto.ConversationDTO;
import com.skillbridge.entity.Conversation;
import com.skillbridge.entity.Message;
import com.skillbridge.repository.ConversationRepository;
import com.skillbridge.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    /**
     * Get all conversations for listing
     */
    public Flux<ConversationDTO> getAllConversations() {
        return Flux.defer(() -> Flux.fromIterable(conversationRepository.findAllConversations()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::convertToDTO);
    }

    /**
     * Convert Conversation entity to DTO with latest message
     */
    private ConversationDTO convertToDTO(Conversation conversation) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setType(conversation.getType());
        dto.setCreatedAt(conversation.getCreatedAt());

        // Fetch latest message for this conversation's session
        if (conversation.getSessionId() != null) {
            Optional<Message> latestMessage = messageRepository.findBySessionIdOrderByTimestampAsc(conversation.getSessionId())
                    .stream()
                    .max(Comparator.comparing(Message::getTimestamp));

            if (latestMessage.isPresent()) {
                Message msg = latestMessage.get();
                dto.setLastMessageId(msg.getId());
                dto.setLastMessage(msg.getContent());
                dto.setLastMessageTime(msg.getTimestamp());
                // Note: otherUserId and otherUserName would need user repository lookup
                // For now, setting senderId as otherUserId (simplified)
                dto.setOtherUserId(msg.getSenderId());
                dto.setOtherUserName("User " + msg.getSenderId()); // Placeholder
            }
        }

        return dto;
    }
}
