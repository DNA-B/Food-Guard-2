package com.dna.fooo_guard.domain.donation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DonationEditRequest {
    // Post 수정용 필드
    private String title;
    private String content;

    // Donation 수정용 필드
    private Long foodId;
}
