package com.dna.fooo_guard.domain.chat.dto;

import java.time.LocalDateTime;

import com.dna.fooo_guard.domain.chat.entity.ChatMessage;
import com.dna.fooo_guard.domain.chat.entity.ChatMessageType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "채팅 메시지 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageResponse {

    @Schema(description = "메시지 ID", example = "1")
    private Long id;

    @Schema(description = "채팅방 ID", example = "1")
    private Long chatRoomId;

    @Schema(description = "발신자 ID", example = "1")
    private Long senderId;

    @Schema(description = "발신자 닉네임", example = "푸드가드")
    private String senderNickname;

    @Schema(description = "메시지 내용", example = "아직 나눔 가능할까요?")
    private String content;

    @Schema(description = "메시지 타입", example = "TEXT", allowableValues = {"TEXT", "SYSTEM"})
    private ChatMessageType type;

    @Schema(description = "생성일시", example = "2026-06-02T10:30:00", type = "string", format = "date-time")
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .chatRoomId(chatMessage.getChatRoom().getId())
                .senderId(chatMessage.getSender().getId())
                .senderNickname(chatMessage.getSender().getNickname())
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
