package com.dna.fooo_guard.domain.chat.dto;

import java.time.LocalDateTime;

import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.entity.ChatRoomStatus;
import com.dna.fooo_guard.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "채팅방 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomResponse {

    @Schema(description = "채팅방 ID", example = "1")
    private Long id;

    @Schema(description = "나눔 ID", example = "1")
    private Long donationId;

    @Schema(description = "나눔 작성자 ID", example = "1")
    private Long ownerId;

    @Schema(description = "요청자 ID", example = "2")
    private Long requesterId;

    @Schema(description = "현재 사용자 기준 상대방 ID", example = "2")
    private Long opponentId;

    @Schema(description = "현재 사용자 기준 상대방 닉네임", example = "나눔요청자")
    private String opponentNickname;

    @Schema(description = "채팅방 상태", example = "OPEN", allowableValues = {"OPEN", "CLOSED"})
    private ChatRoomStatus status;

    @Schema(description = "마지막 메시지", example = "아직 나눔 가능할까요?", nullable = true)
    private String lastMessage;

    @Schema(description = "마지막 메시지 작성일시", example = "2026-06-02T10:30:00", type = "string", format = "date-time", nullable = true)
    private LocalDateTime lastMessageAt;

    @Schema(description = "채팅방 생성일시", example = "2026-06-02T10:00:00", type = "string", format = "date-time")
    private LocalDateTime createdAt;

    public static ChatRoomResponse from(ChatRoom chatRoom, Long currentUserId) {
        User opponent = chatRoom.getOwner().getId().equals(currentUserId)
                ? chatRoom.getRequester()
                : chatRoom.getOwner();

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .donationId(chatRoom.getDonation().getId())
                .ownerId(chatRoom.getOwner().getId())
                .requesterId(chatRoom.getRequester().getId())
                .opponentId(opponent.getId())
                .opponentNickname(opponent.getNickname())
                .status(chatRoom.getStatus())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
