package com.dna.fooo_guard.domain.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageDto;
import com.dna.fooo_guard.domain.chat.service.ChatService;
import com.dna.fooo_guard.global.redis.RedisPublisher;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message) {
        ChatMessageDto processedMessage = chatService.processMessage(message);
        redisPublisher.publish(processedMessage);
    }
}