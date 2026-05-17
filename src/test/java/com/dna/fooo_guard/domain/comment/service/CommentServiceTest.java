package com.dna.fooo_guard.domain.comment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Comment rootComment;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("tester").nickname("테스터").build();
        rootComment = Comment.builder()
                .id(1L)
                .content("부모 댓글")
                .postId(10L)
                .status(CommentStatus.PUBLISHED)
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_Success() {
        CommentCreateRequest dto = CommentCreateRequest.builder().content("댓글").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        commentService.createComment(dto, 10L, 1L);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("대댓글 생성 성공 - 대댓글의 부모는 최상위 댓글로 교체")
    void createComment_ReplyToReplyUsesRootParent() {
        Comment childComment = Comment.builder()
                .id(2L)
                .content("자식 댓글")
                .postId(10L)
                .parentId(1L)
                .status(CommentStatus.PUBLISHED)
                .user(testUser)
                .build();
        CommentCreateRequest dto = CommentCreateRequest.builder().content("대댓글").parentId(2L).build();
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(2L)).thenReturn(Optional.of(childComment));

        commentService.createComment(dto, 10L, 1L);

        verify(commentRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getParentId());
    }

    @Test
    @DisplayName("대댓글 생성 실패 - 부모 댓글의 게시글이 다름")
    void createComment_InvalidParentComment() {
        CommentCreateRequest dto = CommentCreateRequest.builder().content("대댓글").parentId(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(rootComment));

        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.createComment(dto, 999L, 1L));

        assertEquals(ErrorCode.INVALID_PARENT_COMMENT, exception.getErrorCode());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("대댓글 생성 실패 - 부모 댓글 삭제됨")
    void createComment_AlreadyDeletedParentComment() {
        Comment deletedParent = Comment.builder()
                .id(1L)
                .content("삭제 댓글")
                .postId(10L)
                .status(CommentStatus.DELETED)
                .user(testUser)
                .build();
        CommentCreateRequest dto = CommentCreateRequest.builder().content("대댓글").parentId(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(deletedParent));

        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.createComment(dto, 10L, 1L));

        assertEquals(ErrorCode.ALREADY_DELETED_COMMENT, exception.getErrorCode());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("게시글 댓글 전체 조회 성공 - 부모와 자식 구조")
    void findAllCommentByPostId_Success() {
        Comment childComment = Comment.builder()
                .id(2L)
                .content("자식 댓글")
                .postId(10L)
                .parentId(1L)
                .status(CommentStatus.PUBLISHED)
                .user(testUser)
                .build();

        when(commentRepository.findAllByPostId(10L)).thenReturn(List.of(rootComment, childComment));

        List<CommentResponse> responses = commentService.findAllCommentByPostId(10L);

        assertEquals(1, responses.size());
        assertEquals("부모 댓글", responses.get(0).getContent());
        assertEquals(1, responses.get(0).getChildren().size());
        assertEquals("자식 댓글", responses.get(0).getChildren().get(0).getContent());
    }

    @Test
    @DisplayName("댓글 단건 조회 성공")
    void findCommentByIdAndUserId_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(rootComment));

        CommentResponse response = commentService.findCommentByIdAndUserId(1L, 1L);

        assertEquals(1L, response.getId());
        assertEquals("부모 댓글", response.getContent());
    }

    @Test
    @DisplayName("댓글 단건 조회 실패 - 권한 없음")
    void findCommentByIdAndUserId_AccessDenied() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(rootComment));

        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.findCommentByIdAndUserId(1L, 999L));

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void editComment_Success() {
        CommentEditRequest dto = CommentEditRequest.builder().content("수정 댓글").build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(rootComment));

        commentService.editComment(dto, 1L, 1L);

        assertEquals("수정 댓글", rootComment.getContent());
        assertEquals(CommentStatus.EDITED, rootComment.getStatus());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(rootComment));

        commentService.deleteComment(1L, 1L);

        assertEquals(CommentStatus.DELETED, rootComment.getStatus());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 없음")
    void deleteComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> commentService.deleteComment(1L, 1L));

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }
}
