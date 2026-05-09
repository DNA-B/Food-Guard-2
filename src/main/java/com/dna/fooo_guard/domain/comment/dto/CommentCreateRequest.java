package com.dna.fooo_guard.domain.comment.dto;

import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateRequest {
    String content;
    Long parentId;

    public Comment toEntity(User user, Long postId, Long parentId) {
        return Comment.builder()
                .content(this.content)
                .user(user)
                .postId(postId)
                .parentId(parentId)
                .build();
    }
}
