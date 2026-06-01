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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ChatMessage", description = "채팅 메시지 API")
@RestController
@RequestMapping("/api/v1/chatrooms/{chatRoomId}/messages")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅 메시지 목록 조회", description = "채팅방 ID로 메시지 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatMessageResponse.class))))
    @GetMapping
    public ResponseEntity<List<ChatMessageResponse>> findMessages(
            @Parameter(description = "채팅방 ID", example = "1") @PathVariable("chatRoomId") Long chatRoomId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatMessageService.findMessages(chatRoomId, userId));
    }
}
