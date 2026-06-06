package com.dna.fooo_guard.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.comment.entity.CommentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "댓글 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    @Schema(description = "댓글 ID", example = "1")
    private Long id;

    @Schema(description = "댓글 내용", example = "좋은 정보 감사합니다.")
    private String content;

    @Schema(description = "작성자 닉네임", example = "푸드가드")
    private String author;

    @Schema(description = "부모 댓글 ID. 최상위 댓글이면 null", example = "1", nullable = true)
    private Long parentId;

    @Schema(description = "댓글 상태", example = "PUBLISHED", allowableValues = { "PUBLISHED", "EDITED", "DELETED" })
    private CommentStatus status;

    @Schema(description = "수정일시", example = "2026-06-02T10:30:00", type = "string", format = "date-time")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Schema(description = "대댓글 목록")
    private List<CommentResponse> children = new ArrayList<>();

    // Entity -> DTO
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getUser() != null ? comment.getUser().getNickname() : null)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .status(comment.getStatus())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
