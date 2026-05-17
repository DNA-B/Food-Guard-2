package com.dna.fooo_guard.domain.post.service;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("tester")
                .nickname("테스터")
                .build();
        testPost = Post.builder()
                .id(1L)
                .title("기존 제목")
                .content("기존 내용")
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_Success() {
        PostCreateRequest dto = PostCreateRequest.builder().title("제목").content("내용").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        postService.createPost(dto, 1L);

        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 생성 실패 - 유저 없음")
    void createPost_UserNotFound() {
        PostCreateRequest dto = PostCreateRequest.builder().title("제목").content("내용").build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> postService.createPost(dto, 1L));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("전체 게시글 조회 성공")
    void findAllPost_Success() {
        when(postRepository.findAll()).thenReturn(List.of(testPost));

        List<PostResponse> responses = postService.findAllPost();

        assertEquals(1, responses.size());
        assertEquals("기존 제목", responses.get(0).getTitle());
        assertEquals("테스터", responses.get(0).getAuthor());
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    void findPostByIdAndUserId_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        PostResponse response = postService.findPostByIdAndUserId(1L, 1L);

        assertEquals(1L, response.getId());
        assertEquals("기존 제목", response.getTitle());
    }

    @Test
    @DisplayName("게시글 단건 조회 실패 - 권한 없음")
    void findPostByIdAndUserId_AccessDenied() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        CustomException exception = assertThrows(CustomException.class,
                () -> postService.findPostByIdAndUserId(1L, 999L));

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void editPost_Success() {
        PostEditRequest dto = PostEditRequest.builder().title("수정 제목").content("수정 내용").build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        postService.editPost(1L, 1L, dto);

        assertEquals("수정 제목", testPost.getTitle());
        assertEquals("수정 내용", testPost.getContent());
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        postService.deletePost(1L, 1L);

        verify(postRepository).delete(testPost);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 게시글 없음")
    void deletePost_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> postService.deletePost(1L, 1L));

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        verify(postRepository, never()).delete(any(Post.class));
    }
}
