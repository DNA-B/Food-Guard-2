package com.dna.fooo_guard.domain.chat.dto;

import java.time.LocalDateTime;

import com.dna.fooo_guard.domain.chat.entity.ChatMessage;
import com.dna.fooo_guard.domain.chat.entity.ChatMessageType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderNickname;
    private String content;
    private ChatMessageType type;
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
