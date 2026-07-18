package com.dna.fooo_guard.domain.chat.dto;

import com.dna.fooo_guard.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomResponseDto {
    private final Long roomId;
    private final Long donationId;
    private final String opponentNickname; // 내가 호스트면 게스트 닉네임, 내가 게스트면 호스트 닉네임

    public ChatRoomResponseDto(ChatRoom chatRoom, Long currentUserId) {
        this.roomId = chatRoom.getId();
        this.donationId = chatRoom.getDonation().getId();
        this.opponentNickname = chatRoom.getHost().getId().equals(currentUserId)
                ? chatRoom.getGuest().getNickname()
                : chatRoom.getHost().getNickname();
    }
}