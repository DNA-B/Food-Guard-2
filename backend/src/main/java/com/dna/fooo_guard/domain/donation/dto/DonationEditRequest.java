package com.dna.fooo_guard.domain.donation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "나눔 수정 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DonationEditRequest {
    // Post 수정용 필드
    @Schema(description = "나눔 게시글 제목", example = "오늘까지인 샐러드 나눔합니다.")
    private String title;

    @Schema(description = "나눔 게시글 내용", example = "회사 근처에서 직접 가져가실 수 있는 분께 나눔합니다.")
    private String content;

    // Donation 수정용 필드
    @Schema(description = "나눔할 식품 ID", example = "1")
    private Long foodId;
}
