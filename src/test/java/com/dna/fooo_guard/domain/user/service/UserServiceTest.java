package com.dna.fooo_guard.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
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

import com.dna.fooo_guard.domain.user.dto.UserResponse;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("테스트유저")
                .build();
    }

    @Test
    @DisplayName("유저 조회 성공")
    void findUserById_Success() {
        Long userId = testUser.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        UserResponse response = userService.findUserById(userId);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("유저가 없을 때, 예외 발생")
    void findUserById_Fail() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.findUserById(userId);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteUser_Success() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        userService.deleteUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("삭제하려는 유저가 없을 때 예외 발생 및 메시지 검증")
    void deleteUser_Fail() {
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }
}