package com.dna.fooo_guard.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "그룹 수정 요청")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupEditRequest {

    @Schema(description = "그룹명", example = "우리집")
    private String name;

    @Schema(description = "그룹 설명", example = "가족이 함께 식품을 관리하는 그룹")
    private String description;
}
