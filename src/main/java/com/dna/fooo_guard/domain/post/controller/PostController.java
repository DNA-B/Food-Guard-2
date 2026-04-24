package com.dna.fooo_guard.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.post.dto.PostCreateRequest;
import com.dna.fooo_guard.domain.post.dto.PostEditRequest;
import com.dna.fooo_guard.domain.post.dto.PostResponse;
import com.dna.fooo_guard.domain.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody PostCreateRequest dto, @AuthenticationPrincipal Long userId) {
        postService.createPost(dto, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts() {
        return ResponseEntity.ok(postService.findAllPost());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(postService.findPostByIdAndUserId(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editPost(@PathVariable Long id, @AuthenticationPrincipal Long userId,
            @RequestBody PostEditRequest dto) {
        postService.editPost(id, userId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        postService.deletePost(id, userId);
        return ResponseEntity.ok().build();
    }
}
