package com.dna.fooo_guard.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("유저 조회 성공")
    void findUserById_Success() {
        // ------------------ [GIVEN] ------------------
        User fakeUser = User.builder()
                .id(1L)
                .username("testUser")
                .password("password123!")
                .build();

        given(userRepository.findById(fakeUser.getId())).willReturn(Optional.of(fakeUser));

        // ------------------ [WHEN] ------------------
        UserResponse response = userService.findUserById(fakeUser.getId());

        // ------------------ [THEN] ------------------
        assertThat(response.getUsername()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("유저 조회 실패 - 존재하지 않는 유저")
    void findUserById_Fail() {
        // ------------------ [GIVEN] ------------------
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // ------------------ [WHEN & THEN] ------------------
        Throwable thrown = catchThrowable(() -> userService.findUserById(1L));
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customEx = (CustomException) exception;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("유저 탈퇴 성공 - 댓글(소프트) 및 게시글(하드) 연쇄 삭제 루프 검증")
    void deleteUser_Success_WithCommentsAndPosts() {
        // ------------------ [GIVEN] ------------------
        Long userId = 1L;
        given(userRepository.existsById(userId)).willReturn(true);

        Comment fakeComment = mock(Comment.class);
        given(commentRepository.findAllByUserId(userId)).willReturn(List.of(fakeComment));

        Post fakePost = Post.builder().id(100L).title("테스트 글").build();
        given(postRepository.findAllByUserId(userId)).willReturn(List.of(fakePost));

        // 포스트에 달린 댓글
        Comment fakePostComment = mock(Comment.class);
        given(commentRepository.findAllByPostId(fakePost.getId())).willReturn(List.of(fakePostComment));

        // ------------------ [WHEN] ------------------
        userService.deleteUser(userId);

        // ------------------ [THEN] ------------------
        // 소프트 딜리트
        verify(fakeComment, times(1)).delete(); // 유저가 직접 쓴 댓글 소프트 삭제 확인
        verify(fakePostComment, times(1)).delete(); // 유저가 쓴 글에 달린 댓글 소프트 삭제 확인

        // 하드 딜리트
        verify(postRepository, times(1)).delete(fakePost);

        verify(userGroupRepository, times(1)).deleteAllByUserId(userId);
        verify(foodRepository, times(1)).deleteAllByUserId(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("유저 삭제 실패 - 존재하지 않는 유저")
    void deleteUser_Fail() {
        // ------------------ [GIVEN] ------------------
        Long userId = 1L;
        given(userRepository.existsById(userId)).willReturn(false);

        // ------------------ [WHEN & THEN] ------------------
        Throwable thrown = catchThrowable(() -> userService.deleteUser(userId));
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customEx = (CustomException) exception;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }
}
