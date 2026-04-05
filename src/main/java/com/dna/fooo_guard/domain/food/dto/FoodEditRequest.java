package com.dna.fooo_guard.domain.food.dto;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodEditRequest {
      private String name;
      private String type;
      private String description;
      private LocalDate expiryAt;
}
