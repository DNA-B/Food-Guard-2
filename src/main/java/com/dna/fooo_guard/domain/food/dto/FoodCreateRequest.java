package com.dna.fooo_guard.domain.food.dto;

import java.time.LocalDate;

import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.group.entity.Group;
import com.dna.fooo_guard.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodCreateRequest {
    String name;
    String type;
    String description;
    LocalDate expiryAt;
    Long groupId;

    public Food toEntity(User user, Group group) {
        return Food.builder()
                .name(this.name)
                .type(this.type)
                .description(this.description)
                .expiryAt(this.expiryAt)
                .user(user)
                .group(group)
                .build();
    }
}
