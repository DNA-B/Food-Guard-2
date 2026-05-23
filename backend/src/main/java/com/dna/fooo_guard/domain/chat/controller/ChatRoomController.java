package com.dna.fooo_guard.domain.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.chat.dto.ChatRoomResponse;
import com.dna.fooo_guard.domain.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/donations/{donationId}/chatrooms")
    public ResponseEntity<ChatRoomResponse> startChatRoom(@PathVariable("donationId") Long donationId,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatRoomService.startChatRoom(donationId, userId));
    }

    @GetMapping("/chatrooms")
    public ResponseEntity<List<ChatRoomResponse>> findMyChatRooms(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatRoomService.findMyChatRooms(userId));
    }

    @GetMapping("/chatrooms/{id}")
    public ResponseEntity<ChatRoomResponse> findMyChatRoom(@PathVariable("id") Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatRoomService.findMyChatRoom(id, userId));
    }

    @PatchMapping("/chatrooms/{id}/close")
    public ResponseEntity<Void> closeChatRoom(@PathVariable("id") Long id,
            @AuthenticationPrincipal Long userId) {
        chatRoomService.closeChatRoom(id, userId);
        return ResponseEntity.ok().build();
    }
}
