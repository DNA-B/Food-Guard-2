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
@Transactional(readOnly = true)
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

    @Transactional
    public void createComment(CommentCreateRequest dto, Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Long parentId = dto.getParentId();

        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

            if (!parent.getPostId().equals(postId)) {
                throw new CustomException(ErrorCode.INVALID_PARENT_COMMENT);
            }

            if (parent.getStatus() == CommentStatus.DELETED) {
                throw new CustomException(ErrorCode.ALREADY_DELETED_COMMENT);
            }

            // 만약 대댓글을 달려는 대상이 이미 parentId를 갖고 있다면 그것으로 교체
            if (parent.getParentId() != null) {
                parentId = parent.getParentId();
            }
        }

        Comment newComment = dto.toEntity(user, postId, parentId);
        commentRepository.save(newComment);
    }

    // TODO: User 정보 가져올 때, N+1 문제
    public List<CommentResponse> findAllCommentByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        Map<Long, CommentResponse> rootMap = new HashMap<>();
        List<CommentResponse> roots = new ArrayList<>();

        // 최상위 부모들만 먼저 골라내서 Map에 저장
        for (Comment comment : comments) {
            if (comment.getParentId() == null) {
                CommentResponse dto = CommentResponse.from(comment);
                rootMap.put(dto.getId(), dto);
                roots.add(dto);
            }
        }

        // 나머지 자식들 parentId 보고 rootMap에서 부모 찾아서 리스트에 추가
        for (Comment comment : comments) {
            if (comment.getParentId() != null) {
                CommentResponse parentDto = rootMap.get(comment.getParentId());
                if (parentDto != null) {
                    parentDto.getChildren().add(CommentResponse.from(comment));
                }
            }
        }

        return roots;
    }

    public CommentResponse findCommentByIdAndUserId(Long commentId, Long userId) {
        Comment comment = getCommentWithAccessCheck(commentId, userId);
        return CommentResponse.from(comment);
    }

    // dirtyCheking으로 DB 자동 반영하기
    @Transactional
    public void editComment(CommentEditRequest dto, Long commentId, Long userId) {
        Comment comment = getCommentWithAccessCheck(commentId, userId);
        comment.edit(dto);
    }

    // dirtyCheking으로 DB 자동 반영하기
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentWithAccessCheck(commentId, userId);
        comment.delete();
    }
}
