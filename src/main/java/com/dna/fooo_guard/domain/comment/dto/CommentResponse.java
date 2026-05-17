package com.dna.fooo_guard.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.comment.entity.CommentStatus;

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
    private CommentStatus status;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<CommentResponse> children = new ArrayList<>();

    // Entity -> DTO
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .status(comment.getStatus())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
