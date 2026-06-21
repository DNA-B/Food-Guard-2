package com.dna.fooo_guard.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dna.fooo_guard.domain.auth.dto.LoginRequest;
import com.dna.fooo_guard.domain.auth.dto.LoginResponse;
import com.dna.fooo_guard.domain.auth.dto.SignUpRequest;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;
import com.dna.fooo_guard.global.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // ------------------ [GIVEN] ------------------
        SignUpRequest dto = SignUpRequest.builder()
                .username("testUser")
                .password("password123!")
                .nickname("testNickname")
                .build();

        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

        // ------------------ [WHEN] ------------------
        String result = authService.signUp(dto);

        // ------------------ [THEN] ------------------
        assertThat(result).isEqualTo("유저[testUser] - 회원가입");
    }

    @Test
    @DisplayName("회원가입 실패 - username 중복")
    void signUp_Failure_UsernameExists() {
        // ------------------ [GIVEN] ------------------
        SignUpRequest dto = SignUpRequest.builder()
                .username("testUser")
                .password("password123!")
                .nickname("testNickname")
                .build();

        given(userRepository.existsByUsername(anyString())).willReturn(true);

        // ------------------ [WHEN & THEN] ------------------
        Throwable thrown = catchThrowable(() -> authService.signUp(dto));
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customEx = (CustomException) exception;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_USERNAME);
                });
    }

    @Test
    @DisplayName("회원가입 실패 - nickname 중복")
    void signUp_Failure_NicknameExists() {
        // ------------------ [GIVEN] ------------------
        SignUpRequest dto = SignUpRequest.builder()
                .username("testUser")
                .password("password123!")
                .nickname("testNickname")
                .build();

        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByNickname(anyString())).willReturn(true);

        // ------------------ [WHEN & THEN] ------------------
        Throwable thrown = catchThrowable(() -> authService.signUp(dto));
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customEx = (CustomException) exception;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
                });
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {

        // ------------------ [GIVEN] ------------------
        LoginRequest dto = LoginRequest.builder()
                .username("testUser")
                .password("password123!")
                .build();

        User fakeUser = User.builder()
                .id(1L)
                .username("testUser")
                .password("password123!")
                .build();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(fakeUser));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.createToken(anyLong())).willReturn("jwt-token-for-testUser");

        // ------------------ [WHEN] ------------------
        LoginResponse response = authService.login(dto);

        // ------------------ [THEN] ------------------
        assertThat(response.getAccessToken()).isEqualTo("jwt-token-for-testUser");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Failure_UserNotFound() {
        // ------------------ [GIVEN] ------------------
        LoginRequest dto = LoginRequest.builder()
                .username("wrongUser")
                .password("password123!")
                .build();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        // ------------------ [WHEN & THEN] ------------------
        Throwable thrown = catchThrowable(() -> authService.login(dto));
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customEx = (CustomException) exception;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 다름")
    void login_Failure_WrongPassword() {

        // ------------------ [GIVEN] ------------------
        LoginRequest dto = LoginRequest.builder()
                .username("testUser")
                .password("password123!")
                .build();

        User fakeUser = User.builder()
                .id(1L)
                .username("testUser")
                .password("password123!")
                .build();

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(fakeUser));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // ------------------ [WHEN & THEN] ------------------
        Throwable thrown = catchThrowable(() -> authService.login(dto));
        assertThat(thrown)
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customEx = (CustomException) exception;
                    assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
                });
    }
}