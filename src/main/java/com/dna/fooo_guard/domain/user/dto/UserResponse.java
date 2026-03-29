package com.dna.fooo_guard.domain.user.dto;

import com.dna.fooo_guard.domain.user.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String username;
    private final String nickname;

    @Builder
    public UserResponse(Long id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }

    // Entity -> DTO
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }
}