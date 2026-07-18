package com.dna.fooo_guard.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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

import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.comment.repository.CommentRepository;
import com.dna.fooo_guard.domain.post.dto.PostCreateRequest;
import com.dna.fooo_guard.domain.post.dto.PostEditRequest;
import com.dna.fooo_guard.domain.post.dto.PostResponse;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.post.repository.PostRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

        @Mock
        private PostRepository postRepository;
        @Mock
        private UserRepository userRepository;
        @Mock
        private CommentRepository commentRepository;
        @InjectMocks
        private PostService postService;

        // ------------------ [HELPERS] ------------------

        private static User createUser(Long userId) {
                return User.builder()
                                .id(userId)
                                .username("testUser")
                                .build();
        }

        private static Post createPost(Long postId, String title, String content, User user) {
                return Post.builder()
                                .id(postId)
                                .title(title)
                                .content(content)
                                .user(user)
                                .build();
        }

        private static PostCreateRequest createPostCreateRequest() {
                return PostCreateRequest.builder()
                                .title("테스트 제목")
                                .content("테스트 내용")
                                .build();
        }

        private static PostEditRequest createPostEditRequest() {
                return PostEditRequest.builder()
                                .title("수정된 제목")
                                .content("수정된 내용")
                                .build();
        }

        @Nested
        @DisplayName("성공 케이스")
        class Success {

                @Test
                @DisplayName("게시글 생성 성공")
                void createPost_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = createUser(userId);

                        PostCreateRequest request = createPostCreateRequest();

                        given(userRepository.getReferenceById(userId)).willReturn(fakeUser);

                        // ------------------ [WHEN] ------------------
                        postService.createPost(request, userId);

                        // ------------------ [THEN] ------------------
                        verify(userRepository).getReferenceById(userId);

                        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
                        verify(postRepository).save(postCaptor.capture());
                        Post savedPost = postCaptor.getValue();

                        assertThat(savedPost)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("title", "content")
                                        .isEqualTo(request);

                        assertThat(savedPost.getUser().getId()).isEqualTo(userId);
                }

                @Test
                @DisplayName("게시글 전체 조회 성공")
                void findAllPost_Success() {
                        // ------------------ [GIVEN] ------------------
                        User fakeUser = createUser(1L);

                        Post post1 = createPost(100L, "첫 번째 게시글", "내용 1", fakeUser);

                        Post post2 = createPost(200L, "두 번째 게시글", "내용 2", fakeUser);

                        List<Post> fakePosts = List.of(post1, post2);

                        given(postRepository.findAllWithUser()).willReturn(fakePosts);

                        // ------------------ [WHEN] ------------------
                        List<PostResponse> responses = postService.findAllPost();

                        // ------------------ [THEN] ------------------
                        verify(postRepository).findAllWithUser();

                        assertThat(responses).hasSize(2);
                        assertThat(responses)
                                        .extracting("title", "content")
                                        .containsExactly(
                                                        tuple("첫 번째 게시글", "내용 1"),
                                                        tuple("두 번째 게시글", "내용 2"));
                }

                @Test
                @DisplayName("게시글 1건 조회 성공")
                void findPostById_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long postId = 100L;
                        User fakeUser = createUser(1L);

                        Post post = createPost(postId, "맛있는 게시글", "내용내용", fakeUser);

                        given(postRepository.findByIdWithUser(postId)).willReturn(Optional.of(post));

                        // ------------------ [WHEN] ------------------
                        PostResponse response = postService.findPostById(postId);

                        // ------------------ [THEN] ------------------
                        verify(postRepository).findByIdWithUser(postId);

                        assertThat(response)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("title", "content")
                                        .isEqualTo(post);
                }

                @Test
                @DisplayName("게시글 수정 성공")
                void editPost_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        Long postId = 100L;
                        User fakeUser = createUser(userId);

                        Post originPost = createPost(postId, "원래 제목", "원래 내용", fakeUser);

                        PostEditRequest request = createPostEditRequest();

                        given(postRepository.findById(postId)).willReturn(Optional.of(originPost));

                        // ------------------ [WHEN] ------------------
                        postService.editPost(postId, userId, request);

                        // ------------------ [THEN] ------------------
                        verify(postRepository).findById(postId);

                        assertThat(originPost)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("title", "content")
                                        .isEqualTo(request);
                }

                @Test
                @DisplayName("게시글 삭제 성공 - 댓글(소프트) 및 게시글(하드) 연쇄 삭제 루프 검증")
                void deletePost_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        Long postId = 100L;
                        User fakeUser = createUser(userId);
                        Post fakePost = createPost(postId, null, null, fakeUser);

                        Comment fakeComment1 = mock(Comment.class);
                        Comment fakeComment2 = mock(Comment.class);
                        List<Comment> fakeComments = List.of(fakeComment1, fakeComment2);

                        given(postRepository.findById(postId)).willReturn(Optional.of(fakePost));
                        given(commentRepository.findAllByPostId(postId)).willReturn(fakeComments);

                        // ------------------ [WHEN] ------------------
                        postService.deletePost(postId, userId);

                        // ------------------ [THEN] ------------------
                        verify(postRepository).findById(postId);
                        verify(commentRepository).findAllByPostId(postId);

                        verify(fakeComment1, times(1)).delete();
                        verify(fakeComment2, times(1)).delete();
                        verify(postRepository, times(1)).delete(fakePost);
                }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

                @Test
                @DisplayName("게시글 1건 조회 실패 - 게시글 없음")
                void findPostById_Fail_PostNotFound() {
                        // ------------------ [GIVEN] ------------------
                        Long wrongPostId = 100L;

                        given(postRepository.findByIdWithUser(wrongPostId)).willReturn(Optional.empty());

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> postService.findPostById(wrongPostId));

                        verify(postRepository).findByIdWithUser(wrongPostId);

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
                                        });
                }

                @Test
                @DisplayName("게시글 수정 실패 - 권한 없음")
                void editPost_Fail_AccessDenied() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        Long wrongUserId = 2L;
                        Long postId = 100L;
                        User fakeUser = User.builder().id(userId).build();

                        Post fakePost = Post.builder()
                                        .id(postId)
                                        .user(fakeUser)
                                        .build();

                        PostEditRequest request = PostEditRequest.builder()
                                        .title("수정 시도")
                                        .content("내용 시도")
                                        .build();

                        given(postRepository.findById(postId)).willReturn(Optional.of(fakePost));

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> postService.editPost(postId, wrongUserId, request));

                        verify(postRepository).findById(postId);

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
                                        });
                }
        }
}