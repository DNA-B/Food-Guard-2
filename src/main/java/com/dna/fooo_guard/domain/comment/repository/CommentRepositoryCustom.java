package com.dna.fooo_guard.domain.comment.repository;

import java.util.List;

import com.dna.fooo_guard.domain.comment.entity.Comment;

public interface CommentRepositoryCustom {
    List<Comment> findAllByPostIdWithParent(Long postId);
}
