package com.dna.fooo_guard.domain.post.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostEditRequest {
      private String title;
      private String content;
      // TODO: 이미지 파일 수정 기능 추가 시, 이미지 관련 필드도 여기에 추가
}
