package com.dna.fooo_guard.domain.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "그룹명은 필수 입력값입니다.")
    @Size(max = 20, message = "그룹 이름은 20자 이하로 입력해주세요.")
    private String name;

    @Schema(description = "그룹 설명", example = "가족이 함께 식품을 관리하는 그룹")
    @Size(max = 255, message = "그룹 설명은 255자 이하로 입력해주세요.")
    private String description;
}