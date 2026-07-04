package com.dna.fooo_guard.domain.post.dto;

import java.time.LocalDateTime;

import com.dna.fooo_guard.domain.post.entity.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "게시글 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "남는 식재료 보관 팁")
    private String title;

    @Schema(description = "게시글 내용", example = "냉장고 식재료를 오래 보관하는 방법을 공유합니다.")
    private String content;

    @Schema(description = "작성자 닉네임", example = "푸드가드")
    private String author;

    @Schema(description = "작성일시", example = "2026-06-02T10:30:00", type = "string", format = "date-time")
    private LocalDateTime createdAt;
    // TODO: 이미지 URL 추가

    // Entity -> DTO
    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
