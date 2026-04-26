package com.dna.fooo_guard.domain.comment.controller;

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

import com.dna.fooo_guard.domain.comment.dto.CommentCreateRequest;
import com.dna.fooo_guard.domain.comment.dto.CommentEditRequest;
import com.dna.fooo_guard.domain.comment.dto.CommentResponse;
import com.dna.fooo_guard.domain.comment.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(@RequestBody CommentCreateRequest dto, @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        commentService.createComment(dto, postId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.findAllCommentByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Void> editComment(@RequestBody CommentEditRequest dto, @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        commentService.editComment(dto, id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
    }
}
