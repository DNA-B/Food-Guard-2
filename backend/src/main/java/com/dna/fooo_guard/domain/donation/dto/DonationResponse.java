package com.dna.fooo_guard.domain.donation.dto;

import java.time.LocalDateTime;

import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.donation.entity.DonationStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DonationResponse {
    // Post 필드
    private Long postId;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    // Donation 필드
    private Long donationId;
    private Long foodId;
    private String foodName;
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