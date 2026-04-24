package com.dna.fooo_guard.domain.post.dto;

import com.dna.fooo_guard.domain.post.entity.Post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    // TODO: 이미지 URL 추가

    // Entity -> DTO
    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}
