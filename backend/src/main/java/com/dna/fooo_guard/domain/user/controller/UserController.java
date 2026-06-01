package com.dna.fooo_guard.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.user.dto.UserResponse;
import com.dna.fooo_guard.domain.user.service.UserService;
import com.dna.fooo_guard.domain.userGroup.dto.UserGroupResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @GetMapping("/me")
    public ResponseEntity<UserResponse> findUserById(@Parameter(hidden = true) @AuthenticationPrincipal Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자를 삭제 처리합니다.")
    @ApiResponse(responseCode = "200", description = "탈퇴 성공")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@Parameter(hidden = true) @AuthenticationPrincipal Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 그룹 목록 조회", description = "로그인한 사용자가 속한 그룹 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserGroupResponse.class))))
    @GetMapping("/groups")
    public ResponseEntity<List<UserGroupResponse>> findUserGroupsById(
            @Parameter(hidden = true) @AuthenticationPrincipal Long id) {
        return ResponseEntity.ok(userService.findUserGroupsById(id));
    }

}
