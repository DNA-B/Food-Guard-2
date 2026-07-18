package com.dna.fooo_guard.domain.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.chat.dto.ChatRoomResponseDto;
import com.dna.fooo_guard.domain.chat.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ChatRoom", description = "채팅방 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/rooms")
public class ChatRoomController {

    private final ChatService chatService;

    // 채팅방 개설 API
    @Operation(summary = "채팅방 생성", description = "나눔 게시글에 대한 채팅방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED", content = @Content),
            @ApiResponse(responseCode = "404", description = "DONATION_NOT_FOUND / USER_NOT_FOUND", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Long> createRoom(
            @Parameter(description = "나눔 ID", example = "1") @RequestParam Long donationId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        Long roomId = chatService.createChatRoom(donationId, userId);
        return ResponseEntity.ok(roomId);
    }

    // 내 채팅방 목록 조회 API
    @Operation(summary = "내 채팅방 목록 조회", description = "로그인한 사용자의 채팅방 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatRoomResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDto>> getMyRooms(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        List<ChatRoomResponseDto> myChatRooms = chatService.getMyChatRooms(userId);
        return ResponseEntity.ok(myChatRooms);
    }
}
