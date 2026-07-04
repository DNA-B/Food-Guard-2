package com.dna.fooo_guard.domain.user.dto;

import com.dna.fooo_guard.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용자 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 아이디", example = "foodguard01")
    private String username;

    @Schema(description = "사용자 닉네임", example = "푸드가드")
    private String nickname;

    // Entity -> DTO
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }
}
