package com.dna.fooo_guard.domain.group.dto;

import com.dna.fooo_guard.domain.group.entity.Group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "그룹 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupResponse {

    @Schema(description = "그룹 ID", example = "1")
    private Long id;

    @Schema(description = "그룹명", example = "우리집")
    private String name;

    @Schema(description = "그룹 설명", example = "가족이 함께 식품을 관리하는 그룹")
    private String description;

    // Entity -> DTO
    public static GroupResponse from(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .build();
    }
}
