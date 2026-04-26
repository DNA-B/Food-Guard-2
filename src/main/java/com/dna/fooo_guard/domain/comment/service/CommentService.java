package com.dna.fooo_guard.domain.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.comment.dto.CommentCreateRequest;
import com.dna.fooo_guard.domain.comment.dto.CommentEditRequest;
import com.dna.fooo_guard.domain.comment.dto.CommentResponse;
import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.comment.repository.CommentRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // Helper Function start
    private Comment getCommentWithAccessCheck(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return comment;
    }
    // Helper Function end

    public void createComment(CommentCreateRequest dto, Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Comment newComment = dto.toEntity(user, postId, null); // 대댓글 아니니까 parentId는 null
        commentRepository.save(newComment);
    }

    // TODO: User 정보 가져올 때, N+1 문제
    @Transactional(readOnly = true)
    public List<CommentResponse> findAllCommentByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .map(comment -> CommentResponse.from(comment))
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentResponse findCommentByIdAndUserId(Long commentId, Long userId) {
        Comment comment = getCommentWithAccessCheck(commentId, userId);
        return CommentResponse.from(comment);
    }

    // dirtyCheking으로 DB 자동 반영하기
    public void editComment(CommentEditRequest dto, Long commentId, Long userId) {
        Comment comment = getCommentWithAccessCheck(commentId, userId);
        comment.edit(dto);
    }

    // dirtyCheking으로 DB 자동 반영하기
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentWithAccessCheck(commentId, userId);
        comment.delete();
    }
}
