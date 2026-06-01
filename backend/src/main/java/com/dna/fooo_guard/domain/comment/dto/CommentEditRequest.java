package com.dna.fooo_guard.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "댓글 수정 요청")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentEditRequest {

    @Schema(description = "수정할 댓글 내용", example = "수정된 댓글 내용입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
