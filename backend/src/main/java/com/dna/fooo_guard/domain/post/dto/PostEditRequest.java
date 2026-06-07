package com.dna.fooo_guard.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "게시글 수정 요청")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostEditRequest {

      @Schema(description = "게시글 제목", example = "남는 식재료 보관 팁")
      @NotBlank(message = "게시글 제목은 필수 입력값입니다.")
      @Size(max = 20, message = "제목은 20자 이하로 입력해주세요.")
      private String title;

      @Schema(description = "게시글 내용", example = "냉장고 식재료를 오래 보관하는 방법을 공유합니다.")
      @NotBlank(message = "게시글 내용은 필수 입력값입니다.")
      @Size(max = 500, message = "내용은 500자 이하로 입력해주세요.")
      private String content;
      // TODO: 이미지 파일 수정 기능 추가 시, 이미지 관련 필드도 여기에 추가
}