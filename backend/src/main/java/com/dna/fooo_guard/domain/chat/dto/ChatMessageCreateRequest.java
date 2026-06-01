package com.dna.fooo_guard.domain.chat.dto;

import com.dna.fooo_guard.domain.chat.entity.ChatMessage;
import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "채팅 메시지 생성 요청")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageCreateRequest {

    @Schema(description = "메시지 내용", example = "아직 나눔 가능할까요?", maxLength = 500, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 500)
    private String content;

    public ChatMessage toEntity(ChatRoom chatRoom, User sender) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .build();
    }
}
