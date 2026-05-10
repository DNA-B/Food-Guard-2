package com.dna.fooo_guard.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.post.dto.PostCreateRequest;
import com.dna.fooo_guard.domain.post.dto.PostEditRequest;
import com.dna.fooo_guard.domain.post.dto.PostResponse;
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
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Helper Function start
    private Post getPostWithAccessCheck(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return post;
    }
    // Helper Function end

    @Transactional
    public void createPost(PostCreateRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Post newPost = dto.toEntity(user);
        postRepository.save(newPost);
    }

    public List<PostResponse> findAllPost() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> PostResponse.from(post))
                .toList();
    }

    public PostResponse findPostByIdAndUserId(Long postId, Long userId) {
        Post post = getPostWithAccessCheck(postId, userId);
        return PostResponse.from(post);
    }

    // dirtyCheking으로 DB 자동 반영하기
    @Transactional
    public void editPost(Long postId, Long userId, PostEditRequest dto) {
        Post post = getPostWithAccessCheck(postId, userId);
        post.edit(dto);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = getPostWithAccessCheck(postId, userId);
        postRepository.delete(post);
    }

}
