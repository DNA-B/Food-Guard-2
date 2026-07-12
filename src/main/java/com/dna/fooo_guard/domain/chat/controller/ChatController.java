package com.dna.fooo_guard.domain.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageDto;
import com.dna.fooo_guard.domain.chat.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message) {
        ChatMessageDto processedMessage = chatService.processMessage(message);
        messagingTemplate.convertAndSend("/sub/chat/room/" + processedMessage.getRoomId(), processedMessage);
    }
}