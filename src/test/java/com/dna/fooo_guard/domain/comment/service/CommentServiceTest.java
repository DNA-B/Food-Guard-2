package com.dna.fooo_guard.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.post.repository.PostRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

        @Mock
        private CommentRepository commentRepository;
        @Mock
        private UserRepository userRepository;
        @Mock
        private PostRepository postRepository;
        @InjectMocks
        private CommentService commentService;

        // ------------------ [HELPERS] ------------------

        private static User createUser(Long userId) {
                return User.builder().id(userId).build();
        }

        private static Post createPost(Long postId) {
                return Post.builder().id(postId).build();
        }

        private static CommentCreateRequest createCommentCreateRequest(String content, Long parentId) {
                return CommentCreateRequest.builder()
                                .content(content)
                                .parentId(parentId)
                                .build();
        }

        private static CommentEditRequest createCommentEditRequest(String content) {
                return CommentEditRequest.builder()
                                .content(content)
                                .build();
        }

        private static Comment createComment(Long commentId, Post post, CommentStatus status, Comment parent,
                        User user) {
                return Comment.builder()
                                .id(commentId)
                                .post(post)
                                .status(status)
                                .parent(parent)
                                .user(user)
                                .build();
        }

        @Nested
        @DisplayName("성공 케이스")
        class Success {

                @Test
                @DisplayName("부모 댓글 생성 성공")
                void createComment_Root_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        Long postId = 100L;
                        User fakeUser = createUser(userId);
                        Post fakePost = createPost(postId);

                        CommentCreateRequest request = createCommentCreateRequest("새로운 부모 댓글입니다.", null);

                        given(userRepository.getReferenceById(userId)).willReturn(fakeUser);
                        given(postRepository.findById(postId)).willReturn(Optional.of(fakePost));

                        // ------------------ [WHEN] ------------------
                        commentService.createComment(request, postId, userId);

                        // ------------------ [THEN] ------------------
                        verify(userRepository).getReferenceById(userId);
                        verify(postRepository).findById(postId);

                        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
                        verify(commentRepository).save(commentCaptor.capture());
                        Comment savedComment = commentCaptor.getValue();

                        assertThat(savedComment.getContent()).isEqualTo(request.getContent());
                        assertThat(savedComment.getParent()).isNull();
                }

                @Test
                @DisplayName("대댓글 생성 성공 - 깊이 1단계 자식으로 정상 매핑")
                void createComment_Child_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        Long postId = 100L;
                        Long parentId = 10L;
                        User fakeUser = createUser(userId);
                        Post fakePost = createPost(postId);

                        Comment fakeParent = createComment(parentId, fakePost, CommentStatus.PUBLISHED, null, null);

                        CommentCreateRequest request = createCommentCreateRequest("대댓글입니다.", parentId);

                        given(userRepository.getReferenceById(userId)).willReturn(fakeUser);
                        given(postRepository.findById(postId)).willReturn(Optional.of(fakePost));
                        given(commentRepository.findById(parentId)).willReturn(Optional.of(fakeParent));

                        // ------------------ [WHEN] ------------------
                        commentService.createComment(request, postId, userId);

                        // ------------------ [THEN] ------------------
                        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
                        verify(commentRepository).save(commentCaptor.capture());
                        Comment savedComment = commentCaptor.getValue();

                        assertThat(savedComment.getParent()).isEqualTo(fakeParent);
                }

                @Test
                @DisplayName("대댓글 생성 성공 - 3단계 진입 시 최상위 부모로 평탄화(Flattening) 검증")
                void createComment_Flattening_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        Long postId = 100L;
                        Long grandParentId = 9L;
                        Long parentId = 10L;
                        User fakeUser = createUser(userId);
                        Post fakePost = createPost(postId);

                        Comment grandParent = createComment(grandParentId, fakePost, null, null, null);
                        Comment fakeParent = createComment(parentId, fakePost, CommentStatus.PUBLISHED, grandParent,
                                        null);

                        CommentCreateRequest request = createCommentCreateRequest("3단계가 아니라 2단계로 평탄화될 대댓글",
                                        parentId);

                        given(userRepository.getReferenceById(userId)).willReturn(fakeUser);
                        given(postRepository.findById(postId)).willReturn(Optional.of(fakePost));
                        given(commentRepository.findById(parentId)).willReturn(Optional.of(fakeParent));

                        // ------------------ [WHEN] ------------------
                        commentService.createComment(request, postId, userId);

                        // ------------------ [THEN] ------------------
                        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
                        verify(commentRepository).save(commentCaptor.capture());
                        Comment savedComment = commentCaptor.getValue();

                        assertThat(savedComment.getParent()).isEqualTo(grandParent);
                }

                @Test
                @DisplayName("게시글 내 계층형 댓글 전체 조회 성공 - 계층 구조 매핑 검증")
                void findAllCommentByPostId_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long postId = 100L;

                        Comment rootComment = Comment.builder().id(1L).content("부모 댓글").parent(null).build();

                        Comment childComment = Comment.builder().id(2L).content("대댓글").parent(rootComment).build();

                        given(commentRepository.findAllByPostIdWithParent(postId))
                                        .willReturn(List.of(rootComment, childComment));

                        // ------------------ [WHEN] ------------------
                        List<CommentResponse> result = commentService.findAllCommentByPostId(postId);

                        // ------------------ [THEN] ------------------
                        verify(commentRepository).findAllByPostIdWithParent(postId);

                        assertThat(result).hasSize(1);
                        CommentResponse rootResponse = result.get(0);
                        assertThat(rootResponse.getId()).isEqualTo(1L);
                        assertThat(rootResponse.getContent()).isEqualTo("부모 댓글");

                        assertThat(rootResponse.getChildren()).hasSize(1);
                        assertThat(rootResponse.getChildren())
                                        .extracting("id", "content")
                                        .containsExactly(tuple(2L, "대댓글"));
                }

                @Test
                @DisplayName("댓글 수정 성공 - 내용 및 CommentStatus.EDITED 상태 변경 검증")
                void editComment_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long commentId = 10L;
                        Long userId = 1L;
                        User fakeUser = createUser(userId);

                        Comment originComment = Comment.builder()
                                        .id(commentId)
                                        .content("원래 작성한 댓글 내용")
                                        .status(CommentStatus.PUBLISHED)
                                        .user(fakeUser)
                                        .build();

                        CommentEditRequest request = createCommentEditRequest("수정하고 싶은 댓글 내용");

                        given(commentRepository.findById(commentId)).willReturn(Optional.of(originComment));

                        // ------------------ [WHEN] ------------------
                        commentService.editComment(request, commentId, userId);

                        // ------------------ [THEN] ------------------
                        verify(commentRepository).findById(commentId);

                        assertThat(originComment.getContent()).isEqualTo("수정하고 싶은 댓글 내용");
                        assertThat(originComment.getStatus()).isEqualTo(CommentStatus.EDITED);
                }

                @Test
                @DisplayName("댓글 삭제 성공 - CommentStatus.DELETED 소프트 삭제 상태 변경 검증")
                void deleteComment_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long commentId = 10L;
                        Long userId = 1L;
                        User fakeUser = createUser(userId);

                        Comment originComment = createComment(commentId, null, CommentStatus.PUBLISHED, null, fakeUser);

                        given(commentRepository.findById(commentId)).willReturn(Optional.of(originComment));

                        // ------------------ [WHEN] ------------------
                        commentService.deleteComment(commentId, userId);

                        // ------------------ [THEN] ------------------
                        verify(commentRepository).findById(commentId);
                        assertThat(originComment.getStatus()).isEqualTo(CommentStatus.DELETED);
                }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

                @Test
                @DisplayName("댓글 생성 실패 - 존재하지 않는 게시글")
                void createComment_Fail_PostNotFound() {
                        // ------------------ [GIVEN] ------------------
                        Long wrongPostId = 999L;
                        Long userId = 1L;
                        CommentCreateRequest request = createCommentCreateRequest("내용", null);

                        given(postRepository.findById(wrongPostId)).willReturn(Optional.empty());

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(
                                        () -> commentService.createComment(request, wrongPostId, userId));

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                                                        .isEqualTo(ErrorCode.POST_NOT_FOUND));
                }

                @Test
                @DisplayName("대댓글 생성 실패 - 부모 댓글이 존재하지 않음")
                void createComment_Fail_ParentCommentNotFound() {
                        // ------------------ [GIVEN] ------------------
                        Long postId = 100L;
                        Long wrongParentId = 999L;
                        Long userId = 1L;
                        Post fakePost = createPost(postId);

                        CommentCreateRequest request = createCommentCreateRequest("대댓글", wrongParentId);

                        given(postRepository.findById(postId)).willReturn(Optional.of(fakePost));
                        given(commentRepository.findById(wrongParentId)).willReturn(Optional.empty());

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> commentService.createComment(request, postId, userId));

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                                                        .isEqualTo(ErrorCode.COMMENT_NOT_FOUND));
                }

                @Test
                @DisplayName("대댓글 생성 실패 - 부모 댓글이 다른 게시글에 존재함")
                void createComment_Fail_InvalidParentComment() {
                        // ------------------ [GIVEN] ------------------
                        Long postId = 100L;
                        Long otherPostId = 200L;
                        Long parentId = 10L;
                        Long userId = 1L;

                        Post fakePost = createPost(postId);
                        Post otherPost = createPost(otherPostId);
                        Comment fakeParent = createComment(parentId, otherPost, null, null, null);

                        CommentCreateRequest request = createCommentCreateRequest("대댓글", parentId);

                        given(postRepository.findById(postId)).willReturn(Optional.of(fakePost));
                        given(commentRepository.findById(parentId)).willReturn(Optional.of(fakeParent));

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> commentService.createComment(request, postId, userId));

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                                                        .isEqualTo(ErrorCode.INVALID_PARENT_COMMENT));
                }

                @Test
                @DisplayName("대댓글 생성 실패 - 부모 댓글이 이미 삭제(DELETED) 상태임")
                void createComment_Fail_AlreadyDeletedComment() {
                        // ------------------ [GIVEN] ------------------
                        Long postId = 100L;
                        Long parentId = 10L;
                        Long userId = 1L;

                        Post fakePost = createPost(postId);
                        Comment fakeParent = createComment(parentId, fakePost, CommentStatus.DELETED, null, null);

                        CommentCreateRequest request = createCommentCreateRequest("대댓글", parentId);

                        given(postRepository.findById(postId)).willReturn(Optional.of(fakePost));
                        given(commentRepository.findById(parentId)).willReturn(Optional.of(fakeParent));

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> commentService.createComment(request, postId, userId));

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                                                        .isEqualTo(ErrorCode.ALREADY_DELETED_COMMENT));
                }

                @Test
                @DisplayName("댓글 변경/삭제 실패 - 작성자가 일치하지 않아 권한 없음")
                void commentAccessCheck_Fail_AccessDenied() {
                        // ------------------ [GIVEN] ------------------
                        Long commentId = 10L;
                        Long ownerId = 1L;
                        Long wrongId = 2L;

                        User commentOwner = createUser(ownerId);
                        Comment fakeComment = createComment(commentId, null, null, null, commentOwner);

                        given(commentRepository.findById(commentId)).willReturn(Optional.of(fakeComment));

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> commentService.deleteComment(commentId, wrongId));

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                                                        .isEqualTo(ErrorCode.ACCESS_DENIED));
                }
        }
}