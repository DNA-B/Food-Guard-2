package com.dna.fooo_guard.domain.chat.controller;

import com.dna.fooo_guard.domain.chat.dto.ChatRoomResponseDto;
import com.dna.fooo_guard.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/rooms")
public class ChatRoomController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<Long> createRoom(@RequestParam Long donationId, @RequestParam Long userId) {
        Long roomId = chatService.createChatRoom(donationId, userId);
        return ResponseEntity.ok(roomId);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDto>> getMyRooms(@RequestParam Long userId) {
        List<ChatRoomResponseDto> myChatRooms = chatService.getMyChatRooms(userId);
        return ResponseEntity.ok(myChatRooms);
    }
}