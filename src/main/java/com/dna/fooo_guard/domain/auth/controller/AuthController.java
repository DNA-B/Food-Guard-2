package com.dna.fooo_guard.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.auth.dto.CheckNicknameRequest;
import com.dna.fooo_guard.domain.auth.dto.LoginRequest;
import com.dna.fooo_guard.domain.auth.dto.LoginResponse;
import com.dna.fooo_guard.domain.auth.dto.SignUpRequest;
import com.dna.fooo_guard.domain.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "회원가입, 로그인, 닉네임 중복 확인 API")
@SecurityRequirements
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "아이디, 비밀번호, 닉네임으로 새 사용자를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE : 올바르지 않은 입력값 (유효성 검증 실패)", content = @Content),
            @ApiResponse(responseCode = "409", description = "DUPLICATE_USERNAME : 이미 사용 중인 아이디 / DUPLICATE_NICKNAME : 이미 사용 중인 닉네임", content = @Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(dto));
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 JWT access token을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE : 올바르지 않은 입력값 (아이디/비밀번호 누락)", content = @Content),
            @ApiResponse(responseCode = "401", description = "LOGIN_FAILED : 로그인 실패 (아이디 혹은 비밀번호 불일치)", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임을 사용할 수 있는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능 여부 반환 (true: 사용 가능, false: 중복됨)", content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE : 올바르지 않은 입력값 (닉네임 누락 또는 형식 오류)", content = @Content)
    })
    @PostMapping("/check/nickname")
    public ResponseEntity<Boolean> checkNickname(@Valid @RequestBody CheckNicknameRequest dto) {
        return ResponseEntity.ok(authService.isNicknameAvailable(dto));
    }
}