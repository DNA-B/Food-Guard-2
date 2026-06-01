package com.dna.fooo_guard.domain.comment.dto;

import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "댓글 작성 요청")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateRequest {

    @Schema(description = "댓글 내용", example = "좋은 정보 감사합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    String content;

    @Schema(description = "부모 댓글 ID. 일반 댓글이면 null", example = "1", nullable = true)
    Long parentId;

    public Comment toEntity(User user, Post post, Comment parent) {
        return Comment.builder()
                .content(this.content)
                .user(user)
                .post(post)
                .parent(parent)
                .build();
    }
}
