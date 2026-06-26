package com.skillbridge.chat.service;

import com.skillbridge.chat.domain.Message;
import com.skillbridge.chat.dto.MessageDTO;
import com.skillbridge.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class ChatService {

    private final MessageRepository messageRepository;

    public Mono<MessageDTO> saveMessage(MessageDTO messageDTO) {
        return Mono.fromCallable(() -> {
            Message message = new Message();
            message.setSessionId(messageDTO.getSessionId());
            message.setSenderId(messageDTO.getSenderId());
            message.setContent(messageDTO.getContent());
            message.setTimestamp(LocalDateTime.now());
            return messageRepository.save(message);
        }).subscribeOn(Schedulers.boundedElastic()).map(this::convertToDTO);
    }

    public Flux<MessageDTO> getMessagesBySession(Long sessionId) {
        return Flux.defer(() -> Flux.fromIterable(messageRepository.findBySessionIdOrderByTimestampAsc(sessionId))).subscribeOn(Schedulers.boundedElastic()).map(this::convertToDTO);
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSessionId(message.getSessionId());
        dto.setSenderId(message.getSenderId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }
}
