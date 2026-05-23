package com.dna.fooo_guard.domain.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dna.fooo_guard.domain.comment.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByUserId(Long userId);

    List<Comment> findAllByPostId(Long postId);

    Optional<Comment> findByIdAndUserId(Long CommentId, Long userId);

}
