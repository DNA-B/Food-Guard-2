package com.dna.fooo_guard.domain.donation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "나눔 등록 요청")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DonationCreateRequest {

    @Schema(description = "나눔 게시글 제목", example = "오늘까지인 샐러드 나눔합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "나눔 제목은 필수 입력값입니다.")
    private String title;

    @Schema(description = "나눔 게시글 내용", example = "회사 근처에서 직접 가져가실 수 있는 분께 나눔합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "나눔 내용은 필수 입력값입니다.")
    private String content;

    @Schema(description = "나눔할 식품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "나눔할 식품은 필수 선택사항입니다.")
    private Long foodId;
}