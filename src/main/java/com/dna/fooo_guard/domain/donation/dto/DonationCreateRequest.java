package com.dna.fooo_guard.domain.donation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DonationCreateRequest {
    private String title;
    private String content;
    private Long foodId;
}