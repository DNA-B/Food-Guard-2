package com.dna.fooo_guard.domain.food.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "식품 수정 요청")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodEditRequest {

      @Schema(description = "식품명", example = "우유")
      @NotBlank(message = "식품명은 필수 입력값입니다.")
      private String name;

      @Schema(description = "식품 종류", example = "DAIRY")
      @NotBlank(message = "식품 종류는 필수 선택사항입니다.")
      private String type;

      @Schema(description = "식품 설명", example = "개봉하지 않은 저지방 우유")
      private String description;

      @Schema(description = "소비기한", example = "2026-06-30", type = "string", format = "date")
      @NotNull(message = "소비기한은 필수 입력값입니다.")
      @FutureOrPresent(message = "소비기한은 오늘 또는 미래의 날짜여야 합니다.")
      private LocalDate expiryAt;

      @Schema(description = "식품을 공유할 그룹 ID", example = "1")
      private Long groupId;
}
