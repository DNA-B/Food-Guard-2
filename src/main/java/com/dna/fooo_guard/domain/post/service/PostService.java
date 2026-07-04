package com.dna.fooo_guard.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private Post getPostWithAccessCheck(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return post;
    }

    @Transactional
    public void createPost(PostCreateRequest dto, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Post newPost = dto.toEntity(user);
        postRepository.save(newPost);
    }

    // TODO: QueryDSL 속도 측정
    public List<PostResponse> findAllPost() {
        // List<Post> posts = postRepository.findAll();
        List<Post> posts = postRepository.findAllWithUser();
        return posts.stream()
                .map(PostResponse::from)
                .toList();
    }

    // TODO: QueryDSL 속도 측정
    public PostResponse findPostById(Long postId) {
        // Post post = postRepository.findById(postId)
        // .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        return PostResponse.from(post);
    }

    @Transactional
    public void editPost(Long postId, Long userId, PostEditRequest dto) {
        Post post = getPostWithAccessCheck(postId, userId);
        post.edit(dto);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = getPostWithAccessCheck(postId, userId);

        // TODO: 추후 QueryDSL 도입 시 벌크 업데이트 연산으로 마이그레이션 예정
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        for (Comment comment : comments) {
            comment.delete();
        }

        postRepository.delete(post);
    }
}