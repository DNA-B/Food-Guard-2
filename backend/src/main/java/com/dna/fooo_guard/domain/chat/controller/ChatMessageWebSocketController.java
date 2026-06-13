package com.dna.fooo_guard.domain.chat.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageCreateRequest;
import com.dna.fooo_guard.domain.chat.dto.ChatMessageResponse;
import com.dna.fooo_guard.domain.chat.service.ChatMessageService;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatMessageWebSocketController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chatrooms/{id}/messages")
    public void sendMessage(@DestinationVariable("id") Long id,
            @Valid @Payload ChatMessageCreateRequest dto,
            Principal principal) {
        Long senderId = getUserId(principal);
        ChatMessageResponse response = chatMessageService.createMessage(id, senderId, dto);

        messagingTemplate.convertAndSend("/sub/chatrooms/" + id, response);
    }

    private Long getUserId(Principal principal) {
        if (!(principal instanceof Authentication authentication) || !(authentication.getPrincipal() instanceof Long userId)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        return userId;
    }
}
