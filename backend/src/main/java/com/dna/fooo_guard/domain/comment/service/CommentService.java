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
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.post.repository.PostRepository;
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
    private final PostRepository postRepository;

    private Comment getCommentWithAccessCheck(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return comment;
    }

    @Transactional
    public void createComment(CommentCreateRequest dto, Long postId, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Comment parent = null;

        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

            if (!parent.getPost().getId().equals(postId)) {
                throw new CustomException(ErrorCode.INVALID_PARENT_COMMENT);
            }

            if (parent.getStatus() == CommentStatus.DELETED) {
                throw new CustomException(ErrorCode.ALREADY_DELETED_COMMENT);
            }

            // 대댓글의 깊이를 최대 2단계(부모-자식)로 제한하기 위한 평탄화 작업
            if (parent.getParent() != null) {
                parent = parent.getParent();
            }
        }

        Comment newComment = dto.toEntity(user, post, parent);
        commentRepository.save(newComment);
    }

    public List<CommentResponse> findAllCommentByPostId(Long postId) {
        // TODO: N+1
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        Map<Long, CommentResponse> rootMap = new HashMap<>();
        List<CommentResponse> roots = new ArrayList<>();

        // 최상위 부모 댓글 먼저 골라내서 Map 및 결과 리스트에 세팅
        for (Comment comment : comments) {
            if (comment.getParent() == null) {
                CommentResponse dto = CommentResponse.from(comment);
                rootMap.put(dto.getId(), dto);
                roots.add(dto);
            }
        }

        // 부모 DTO의 children 리스트에 추가
        for (Comment comment : comments) {
            if (comment.getParent() != null) {
                CommentResponse parentDto = rootMap.get(comment.getParent().getId());
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

    @Transactional
    public void editComment(CommentEditRequest dto, Long commentId, Long userId) {
        Comment comment = getCommentWithAccessCheck(commentId, userId);
        comment.edit(dto);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentWithAccessCheck(commentId, userId);
        comment.delete();
    }
}
