package com.dna.fooo_guard.domain.food.dto;

import java.time.LocalDate;

import com.dna.fooo_guard.domain.food.entity.Food;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodResponse {
    private Long id;
    private String name;
    private String type;
    private String description;
    private LocalDate expiryAt;

    // Entity -> DTO
    public static FoodResponse from(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .type(food.getType())
                .description(food.getDescription())
                .expiryAt(food.getExpiryAt())
                .build();
    }
}
