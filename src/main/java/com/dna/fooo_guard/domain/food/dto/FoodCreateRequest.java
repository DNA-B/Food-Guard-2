package com.dna.fooo_guard.domain.food.dto;

import java.time.LocalDate;

import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodCreateRequest {
    String name;
    String type;
    String description;
    LocalDate expiryAt;
    // TODO: groupId

    public Food toEntity(User user) {
        return Food.builder()
                .name(this.name)
                .type(this.type)
                .description(this.description)
                .expiryAt(this.expiryAt)
                .user(user)
                .build();
    }
}
