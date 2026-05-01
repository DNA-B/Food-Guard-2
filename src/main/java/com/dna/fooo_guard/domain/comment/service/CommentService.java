package com.dna.fooo_guard.domain.comment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.comment.dto.CommentCreateRequest;
import com.dna.fooo_guard.domain.comment.dto.CommentEditRequest;
import com.dna.fooo_guard.domain.comment.dto.CommentResponse;
import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.comment.entity.CommentStatus;
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

        // dto.getParentId()를 사용하여 검증
        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

            // 게시글 일치 확인
            if (!parent.getPostId().equals(postId)) {
                throw new CustomException(ErrorCode.INVALID_PARENT_COMMENT);
            }

            // 부모 삭제 여부 확인
            if (parent.getStatus() == CommentStatus.DELETED) {
                throw new CustomException(ErrorCode.ALREADY_DELETED_COMMENT);
            }
        }

        Comment newComment = dto.toEntity(user, postId);
        commentRepository.save(newComment);
    }

    // TODO: User 정보 가져올 때, N+1 문제
    @Transactional(readOnly = true)
    public List<CommentResponse> findAllCommentByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        Map<Long, CommentResponse> map = new HashMap<>();
        List<CommentResponse> rootResponses = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponse dto = CommentResponse.from(comment);
            map.put(dto.getId(), dto);

            if (comment.getParentId() == null) {
                // 최상위 댓글인 경우
                rootResponses.add(dto);
            } else {
                // 자식 댓글인 경우
                CommentResponse parentDto = map.get(comment.getParentId());
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                }
            }
        }

        return rootResponses;
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
