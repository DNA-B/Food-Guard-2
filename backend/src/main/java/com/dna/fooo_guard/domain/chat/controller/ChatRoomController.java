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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ChatRoom", description = "채팅방 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 시작", description = "나눔 게시글에 대한 채팅방을 시작하거나 기존 채팅방을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "시작 성공", content = @Content(schema = @Schema(implementation = ChatRoomResponse.class)))
    @PostMapping("/donations/{donationId}/chatrooms")
    public ResponseEntity<ChatRoomResponse> startChatRoom(
            @Parameter(description = "나눔 ID", example = "1") @PathVariable("donationId") Long donationId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatRoomService.startChatRoom(donationId, userId));
    }

    @Operation(summary = "내 채팅방 목록 조회", description = "로그인한 사용자의 채팅방 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatRoomResponse.class))))
    @GetMapping("/chatrooms")
    public ResponseEntity<List<ChatRoomResponse>> findMyChatRooms(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatRoomService.findMyChatRooms(userId));
    }

    @Operation(summary = "내 채팅방 단건 조회", description = "채팅방 ID로 로그인한 사용자의 채팅방 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ChatRoomResponse.class)))
    @GetMapping("/chatrooms/{id}")
    public ResponseEntity<ChatRoomResponse> findMyChatRoom(
            @Parameter(description = "채팅방 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatRoomService.findMyChatRoom(id, userId));
    }

    @Operation(summary = "채팅방 종료", description = "채팅방을 종료 상태로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "종료 성공")
    @PatchMapping("/chatrooms/{id}/close")
    public ResponseEntity<Void> closeChatRoom(
            @Parameter(description = "채팅방 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        chatRoomService.closeChatRoom(id, userId);
        return ResponseEntity.ok().build();
    }
}
