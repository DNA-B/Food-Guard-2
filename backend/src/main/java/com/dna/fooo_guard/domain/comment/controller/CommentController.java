package com.dna.fooo_guard.domain.comment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Comment", description = "댓글/대댓글 작성, 조회, 수정, 삭제 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글/대댓글 작성", description = "게시글에 댓글을 작성합니다. parentId를 포함하면 대댓글이 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "작성 성공 (Created)"),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE : 올바르지 않은 입력값 / INVALID_PARENT_COMMENT : 유효하지 않은 부모 댓글 (대댓글 작성 시 부모 댓글이 없거나 다른 게시글인 경우)", content = @Content),
            @ApiResponse(responseCode = "404", description = "POST_NOT_FOUND : 해당 게시글을 찾을 수 없음 / USER_NOT_FOUND : 인증된 유저를 찾을 수 없음", content = @Content)
    })
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> createComment(
            @Valid @RequestBody CommentCreateRequest dto,
            @Parameter(description = "게시글 ID", example = "1") @PathVariable("postId") Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        commentService.createComment(dto, postId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 모든 댓글과 대댓글을 계층형 구조로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class)))),
            @ApiResponse(responseCode = "404", description = "POST_NOT_FOUND : 해당 게시글을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable("postId") Long postId) {
        List<CommentResponse> comments = commentService.findAllCommentByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "댓글 수정", description = "댓글 작성자가 댓글 내용을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공 (OK)"),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE : 올바르지 않은 입력값 (내용 누락 등)", content = @Content),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED : 접근 권한이 없습니다 (본인이 작성한 댓글이 아님)", content = @Content),
            @ApiResponse(responseCode = "404", description = "COMMENT_NOT_FOUND : 해당 댓글을 찾을 수 없음", content = @Content)
    })
    @PutMapping("/comments/{id}")
    public ResponseEntity<Void> editComment(@Valid @RequestBody CommentEditRequest dto,
            @Parameter(description = "댓글 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        commentService.editComment(dto, id, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 삭제", description = "댓글 작성자가 댓글을 삭제합니다. (소프트 삭제 처리로 상태가 DELETED로 변경될 수 있습니다.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공 (OK)"),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED : 접근 권한이 없습니다 (본인이 작성한 댓글이 아님)", content = @Content),
            @ApiResponse(responseCode = "404", description = "COMMENT_NOT_FOUND : 해당 댓글을 찾을 수 없음", content = @Content)
    })
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
    }
}