package com.dna.fooo_guard.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    // 입장, 대화
    public enum MessageType {
        ENTER, TALK
    }

    private MessageType type;
    private Long roomId;
    private Long senderId;
    private String message;
}