package com.dna.fooo_guard.domain.donation.dto;

import java.time.LocalDateTime;

import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.donation.entity.DonationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "나눔 응답")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DonationResponse {
    // Post 필드
    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "나눔 게시글 제목", example = "오늘까지인 샐러드 나눔합니다.")
    private String title;

    @Schema(description = "나눔 게시글 내용", example = "회사 근처에서 직접 가져가실 수 있는 분께 나눔합니다.")
    private String content;

    @Schema(description = "작성자 닉네임", example = "푸드가드")
    private String author;

    @Schema(description = "작성일시", example = "2026-06-02T10:30:00", type = "string", format = "date-time")
    private LocalDateTime createdAt;

    // Donation 필드
    @Schema(description = "나눔 ID", example = "1")
    private Long donationId;

    @Schema(description = "식품 ID", example = "1")
    private Long foodId;

    @Schema(description = "식품명", example = "샐러드")
    private String foodName;

    @Schema(description = "나눔 상태", example = "ONGOING", allowableValues = {"ONGOING", "COMPLETED"})
    private DonationStatus status;

    public static DonationResponse from(Donation donation) {
        return DonationResponse.builder()
                .postId(donation.getPost().getId())
                .title(donation.getPost().getTitle())
                .content(donation.getPost().getContent())
                .author(donation.getPost().getUser().getNickname())
                .createdAt(donation.getCreatedAt())
                .donationId(donation.getId())
                .foodId(donation.getFood().getId())
                .foodName(donation.getFood().getName())
                .status(donation.getStatus())
                .build();
    }
}
