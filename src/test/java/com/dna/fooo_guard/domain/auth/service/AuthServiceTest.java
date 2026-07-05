package com.dna.fooo_guard.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

        @Nested
        @DisplayName("성공 케이스")
        class Success {

                @Test
                @DisplayName("회원가입 성공")
                void signUp_Success() {
                        // ------------------ [GIVEN] ------------------
                        SignUpRequest dto = SignUpRequest.builder()
                                        .username("testUser")
                                        .password("password123!")
                                        .nickname("testNickname")
                                        .build();

                        given(userRepository.existsByUsername(dto.getUsername())).willReturn(false);
                        given(userRepository.existsByNickname(dto.getNickname())).willReturn(false);
                        given(passwordEncoder.encode(dto.getPassword())).willReturn("encodedPassword");

                        // ------------------ [WHEN] ------------------
                        String result = authService.signUp(dto);

                        // ------------------ [THEN] ------------------
                        verify(userRepository).existsByUsername(dto.getUsername());
                        verify(userRepository).existsByNickname(dto.getNickname());
                        verify(passwordEncoder).encode(dto.getPassword());

                        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                        verify(userRepository).save(userCaptor.capture());
                        User savedUser = userCaptor.getValue();

                        assertThat(result).isEqualTo(String.format("유저[%s] - 회원가입", dto.getUsername()));
                        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
                        assertThat(savedUser)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("username", "nickname")
                                        .isEqualTo(dto);
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
                                        .password("encodedPassword")
                                        .build();

                        given(userRepository.findByUsername(dto.getUsername())).willReturn(Optional.of(fakeUser));
                        given(passwordEncoder.matches(dto.getPassword(), fakeUser.getPassword())).willReturn(true);
                        given(jwtTokenProvider.createToken(fakeUser.getId())).willReturn("acceess-token");

                        // ------------------ [WHEN] ------------------
                        LoginResponse response = authService.login(dto);

                        // ------------------ [THEN] ------------------
                        verify(userRepository).findByUsername(dto.getUsername());
                        verify(passwordEncoder).matches(dto.getPassword(), fakeUser.getPassword());
                        verify(jwtTokenProvider).createToken(fakeUser.getId());

                        assertThat(response.getAccessToken()).isEqualTo("acceess-token");
                }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

                @Test
                @DisplayName("회원가입 실패 - username 중복")
                void signUp_Failure_UsernameExists() {
                        // ------------------ [GIVEN] ------------------
                        SignUpRequest dto = SignUpRequest.builder()
                                        .username("testUser")
                                        .password("password123!")
                                        .nickname("testNickname")
                                        .build();

                        given(userRepository.existsByUsername(dto.getUsername())).willReturn(true);

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> authService.signUp(dto));
                        verify(userRepository).existsByUsername(dto.getUsername());
                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode())
                                                                .isEqualTo(ErrorCode.DUPLICATE_USERNAME);
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

                        given(userRepository.existsByUsername(dto.getUsername())).willReturn(false);
                        given(userRepository.existsByNickname(dto.getNickname())).willReturn(true);

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> authService.signUp(dto));
                        verify(userRepository).existsByUsername(dto.getUsername());
                        verify(userRepository).existsByNickname(dto.getNickname());
                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode())
                                                                .isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
                                        });
                }

                @Test
                @DisplayName("로그인 실패 - 존재하지 않는 사용자")
                void login_Failure_UserNotFound() {
                        // ------------------ [GIVEN] ------------------
                        LoginRequest dto = LoginRequest.builder()
                                        .username("wrongUser")
                                        .password("password123!")
                                        .build();

                        given(userRepository.findByUsername(dto.getUsername())).willReturn(Optional.empty());

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> authService.login(dto));
                        verify(userRepository).findByUsername(dto.getUsername());
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
                                        .password("wrongPassword123!")
                                        .build();

                        User fakeUser = User.builder()
                                        .id(1L)
                                        .username("testUser")
                                        .password("encodedPassword")
                                        .build();

                        given(userRepository.findByUsername(dto.getUsername())).willReturn(Optional.of(fakeUser));
                        given(passwordEncoder.matches(dto.getPassword(), fakeUser.getPassword())).willReturn(false);

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> authService.login(dto));
                        verify(userRepository).findByUsername(dto.getUsername());
                        verify(passwordEncoder).matches(dto.getPassword(), fakeUser.getPassword());
                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
                                        });
                }
        }
}