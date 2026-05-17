package com.dna.fooo_guard.domain.chat.dto;

import java.time.LocalDateTime;

import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.entity.ChatRoomStatus;
import com.dna.fooo_guard.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomResponse {
    private Long id;
    private Long donationId;
    private Long ownerId;
    private Long requesterId;
    private Long opponentId;
    private String opponentNickname;
    private ChatRoomStatus status;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
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
