package com.dna.fooo_guard.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "닉네임 중복 확인 요청")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckNicknameRequest {

    @Schema(description = "중복 확인할 닉네임", example = "푸드가드", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;
}
