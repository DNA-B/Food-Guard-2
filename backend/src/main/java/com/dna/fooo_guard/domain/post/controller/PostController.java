package com.dna.fooo_guard.domain.post.controller;

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

import com.dna.fooo_guard.domain.post.dto.PostCreateRequest;
import com.dna.fooo_guard.domain.post.dto.PostEditRequest;
import com.dna.fooo_guard.domain.post.dto.PostResponse;
import com.dna.fooo_guard.domain.post.service.PostService;

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

@Tag(name = "Post", description = "게시글 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시글 작성", description = "로그인한 사용자가 게시글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE", content = @Content),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody PostCreateRequest dto,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        postService.createPost(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "게시글 목록 조회", description = "전체 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts() {
        return ResponseEntity.ok(postService.findAllPost());
    }

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "404", description = "POST_NOT_FOUND", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.findPostById(id));
    }

    @Operation(summary = "게시글 수정", description = "게시글 작성자가 게시글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE", content = @Content),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED", content = @Content),
            @ApiResponse(responseCode = "404", description = "POST_NOT_FOUND", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> editPost(@Parameter(description = "게시글 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostEditRequest dto) {
        postService.editPost(id, userId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 삭제", description = "게시글 작성자가 게시글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED", content = @Content),
            @ApiResponse(responseCode = "404", description = "POST_NOT_FOUND", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        postService.deletePost(id, userId);
        return ResponseEntity.ok().build();
    }
}