package com.dna.fooo_guard.domain.group.controller;

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

import com.dna.fooo_guard.domain.group.dto.GroupCreateRequest;
import com.dna.fooo_guard.domain.group.dto.GroupEditRequest;
import com.dna.fooo_guard.domain.group.dto.GroupResponse;
import com.dna.fooo_guard.domain.group.service.GroupService;

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

@Tag(name = "Group", description = "그룹 관리 API")
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @Operation(summary = "그룹 생성", description = "로그인한 사용자가 새 그룹을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE", content = @Content),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> createGroup(@Valid @RequestBody GroupCreateRequest dto,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        groupService.createGroup(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "내 그룹 목록 조회", description = "로그인한 사용자가 속한 그룹 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GroupResponse.class)))),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<List<GroupResponse>> getGroups(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(groupService.findAllByUserId(userId));
    }

    @Operation(summary = "그룹 단건 조회", description = "그룹 ID로 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "404", description = "GROUP_NOT_FOUND", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroup(
            @Parameter(description = "그룹 ID", example = "1") @PathVariable("id") Long id) {
        return ResponseEntity.ok(groupService.findGroupById(id));
    }

    @Operation(summary = "그룹 수정", description = "그룹 관리자가 그룹 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE", content = @Content),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED", content = @Content),
            @ApiResponse(responseCode = "404", description = "GROUP_NOT_FOUND", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> editGroup(@Parameter(description = "그룹 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody GroupEditRequest dto) {
        groupService.editGroup(id, userId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "그룹 탈퇴", description = "로그인한 사용자가 그룹에서 탈퇴합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "NO_REMAINING_MEMBER / NOT_A_GROUP_MEMBER / INVALID_MANAGER_ASSIGNMENT", content = @Content),
            @ApiResponse(responseCode = "404", description = "GROUP_NOT_FOUND", content = @Content)
    })
    @PostMapping("/{id}/exit")
    public ResponseEntity<Void> groupExit(@Parameter(description = "그룹 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        groupService.groupExit(id, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "그룹 삭제", description = "그룹 관리자가 그룹을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED", content = @Content),
            @ApiResponse(responseCode = "404", description = "GROUP_NOT_FOUND", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "그룹 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        groupService.deleteGroup(id, userId);
        return ResponseEntity.ok().build();
    }
}