package com.dna.fooo_guard.domain.comment.dto;

import java.time.LocalDateTime;

import com.dna.fooo_guard.domain.comment.entity.Comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {
    private Long id;
    private String content;
    private Long parentId;
    private LocalDateTime updatedAt;

    // Entity -> DTO
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
