package com.dna.fooo_guard.domain.auth.dto;

import com.dna.fooo_guard.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequest {

    private String username;
    private String password;
    private String nickname;

    public User toEntity() {
        return User.builder()
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .build();
    }
}