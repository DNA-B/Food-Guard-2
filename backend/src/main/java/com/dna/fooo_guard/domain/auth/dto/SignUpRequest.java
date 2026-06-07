package com.dna.fooo_guard.domain.auth.dto;

import com.dna.fooo_guard.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String username;

    @Schema(description = "로그인 비밀번호", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    // TODO: @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @Schema(description = "서비스에 표시될 닉네임", example = "푸드가드", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    public User toEntity() {
        return User.builder()
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .build();
    }
}