package com.dna.fooo_guard.domain.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageResponse;
import com.dna.fooo_guard.domain.chat.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chatrooms/{chatRoomId}/messages")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @GetMapping
    public ResponseEntity<List<ChatMessageResponse>> findMessages(@PathVariable("chatRoomId") Long chatRoomId,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatMessageService.findMessages(chatRoomId, userId));
    }
}
