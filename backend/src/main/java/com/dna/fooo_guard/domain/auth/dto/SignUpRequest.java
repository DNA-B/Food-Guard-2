package com.dna.fooo_guard.domain.auth.dto;

import com.dna.fooo_guard.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 요청")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpRequest {

    @Schema(description = "로그인에 사용할 사용자 아이디", example = "foodguard01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "로그인 비밀번호", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "서비스에 표시될 닉네임", example = "푸드가드", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    public User toEntity() {
        return User.builder()
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .build();
    }
}
