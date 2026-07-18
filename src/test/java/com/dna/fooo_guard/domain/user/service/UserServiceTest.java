package com.dna.fooo_guard.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.comment.repository.CommentRepository;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.post.repository.PostRepository;
import com.dna.fooo_guard.domain.user.dto.UserResponse;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.domain.userGroup.repository.UserGroupRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

        @Mock
        private UserRepository userRepository;
        @Mock
        private UserGroupRepository userGroupRepository;
        @Mock
        private FoodRepository foodRepository;
        @Mock
        private PostRepository postRepository;
        @Mock
        private CommentRepository commentRepository;
        @InjectMocks
        private UserService userService;

        // ------------------ [HELPERS] ------------------

        private static User createUser(Long userId) {
                return User.builder()
                                .id(userId)
                                .username("testUser")
                                .nickname("Test User")
                                .build();
        }

        private static Post createPost(Long postId) {
                return Post.builder()
                                .id(postId)
                                .title("테스트 글")
                                .build();
        }

        @Nested
        @DisplayName("성공 케이스")
        class Success {

                @Test
                @DisplayName("유저 조회 성공")
                void findUserById_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = createUser(userId);

                        given(userRepository.findById(userId)).willReturn(Optional.of(fakeUser));

                        // ------------------ [WHEN] ------------------
                        UserResponse response = userService.findUserById(userId);

                        // ------------------ [THEN] ------------------
                        verify(userRepository).findById(userId);
                        assertThat(response)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("username", "nickname")
                                        .isEqualTo(fakeUser);
                }

                @Test
                @DisplayName("유저 탈퇴 성공 - 댓글(소프트) 및 게시글(하드) 연쇄 삭제 루프 검증")
                void deleteUser_Success_WithCommentsAndPosts() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        given(userRepository.existsById(userId)).willReturn(true);

                        Comment fakeComment = mock(Comment.class);
                        given(commentRepository.findAllByUserId(userId)).willReturn(List.of(fakeComment));

                        Long postId = 100L;
                        Post fakePost = createPost(postId);
                        given(postRepository.findAllByUserId(userId)).willReturn(List.of(fakePost));

                        Comment fakePostComment = mock(Comment.class);
                        given(commentRepository.findAllByPostId(fakePost.getId())).willReturn(List.of(fakePostComment));

                        // ------------------ [WHEN] ------------------
                        userService.deleteUser(userId);

                        // ------------------ [THEN] ------------------
                        verify(userRepository).existsById(userId);
                        verify(commentRepository).findAllByUserId(userId);
                        verify(postRepository).findAllByUserId(userId);
                        verify(commentRepository).findAllByPostId(fakePost.getId());

                        verify(fakeComment, times(1)).delete();
                        verify(fakePostComment, times(1)).delete();
                        verify(postRepository, times(1)).delete(fakePost);
                        verify(userGroupRepository, times(1)).deleteAllByUserId(userId);
                        verify(foodRepository, times(1)).deleteAllByUserId(userId);
                        verify(userRepository, times(1)).deleteById(userId);
                }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

                @Test
                @DisplayName("유저 조회 실패 - 존재하지 않는 유저")
                void findUserById_Fail() {
                        // ------------------ [GIVEN] ------------------
                        Long wrongId = 999999L;
                        given(userRepository.findById(wrongId)).willReturn(Optional.empty());

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> userService.findUserById(wrongId));
                        verify(userRepository).findById(wrongId);
                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                                        });
                }

                @Test
                @DisplayName("유저 삭제 실패 - 존재하지 않는 유저")
                void deleteUser_Fail() {
                        // ------------------ [GIVEN] ------------------
                        Long wrongId = 999999L;
                        given(userRepository.existsById(wrongId)).willReturn(false);

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> userService.deleteUser(wrongId));
                        verify(userRepository).existsById(wrongId);
                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                                        });
                }
        }
}