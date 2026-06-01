package com.dna.fooo_guard.domain.food.dto;

import java.time.LocalDate;

import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.group.entity.Group;
import com.dna.fooo_guard.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "식품 등록 요청")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodCreateRequest {

    @Schema(description = "식품명", example = "우유", requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @Schema(description = "식품 종류", example = "DAIRY", requiredMode = Schema.RequiredMode.REQUIRED)
    String type;

    @Schema(description = "식품 설명", example = "개봉하지 않은 저지방 우유")
    String description;

    @Schema(description = "소비기한", example = "2026-06-30", type = "string", format = "date", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate expiryAt;

    @Schema(description = "식품을 공유할 그룹 ID", example = "1")
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
