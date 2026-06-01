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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Comment", description = "게시글 댓글 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시글에 댓글 또는 대댓글을 작성합니다.")
    @ApiResponse(responseCode = "200", description = "작성 성공")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(@RequestBody CommentCreateRequest dto,
            @Parameter(description = "게시글 ID", example = "1") @PathVariable("postId") Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        commentService.createComment(dto, postId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글 ID로 댓글과 대댓글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class))))
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable("postId") Long postId) {
        List<CommentResponse> comments = commentService.findAllCommentByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "댓글 수정", description = "댓글 작성자가 댓글 내용을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/comments/{id}")
    public ResponseEntity<Void> editComment(@RequestBody CommentEditRequest dto,
            @Parameter(description = "댓글 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        commentService.editComment(dto, id, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 삭제", description = "댓글 작성자가 댓글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@Parameter(description = "댓글 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
    }
}
