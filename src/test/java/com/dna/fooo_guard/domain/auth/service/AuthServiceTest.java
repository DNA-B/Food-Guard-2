package com.dna.fooo_guard.domain.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("tester")
                .password("encoded-password")
                .nickname("테스터")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        SignUpRequest dto = signUpRequest("tester", "raw-password", "테스터");

        when(userRepository.existsByUsername("tester")).thenReturn(false);
        when(passwordEncoder.encode("raw-password")).thenReturn("encoded-password");

        String result = authService.signUp(dto);

        assertEquals("유저[tester] - 회원가입", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 아이디")
    void signUp_DuplicateUsername() {
        SignUpRequest dto = signUpRequest("tester", "raw-password", "테스터");

        when(userRepository.existsByUsername("tester")).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> authService.signUp(dto));

        assertEquals(ErrorCode.DUPLICATE_USERNAME, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        LoginRequest dto = loginRequest("tester", "raw-password");

        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("raw-password", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.createToken(1L)).thenReturn("access-token");

        LoginResponse response = authService.login(dto);

        assertEquals("access-token", response.getAccessToken());
    }

    @Test
    @DisplayName("로그인 실패 - 유저 없음")
    void login_UserNotFound() {
        LoginRequest dto = loginRequest("missing", "raw-password");

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(dto));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_InvalidPassword() {
        LoginRequest dto = loginRequest("tester", "wrong-password");

        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(dto));

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, exception.getErrorCode());
    }

    private SignUpRequest signUpRequest(String username, String password, String nickname) {
        return SignUpRequest.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();
    }

    private LoginRequest loginRequest(String username, String password) {
        return LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }
}
