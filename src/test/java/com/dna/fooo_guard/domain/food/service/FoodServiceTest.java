package com.dna.fooo_guard.domain.food.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.group.repository.GroupRepository;
import com.dna.fooo_guard.domain.user.dto.UserResponse;
import com.dna.fooo_guard.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupRepository groupRepository;
    @InjectMocks
    private FoodService foodService;

    @Nested
    @DisplayName("성공 케이스")
    class Success {
        @Test
        @DisplayName("유저 조회 성공")
        void findUserById_Success() {
            // ------------------ [GIVEN] ------------------
            User fakeUser = User.builder()
                    .id(1L)
                    .username("testUser")
                    .password("password123!")
                    .build();

            given(userRepository.findById(fakeUser.getId())).willReturn(Optional.of(fakeUser));

            // ------------------ [WHEN] ------------------
            UserResponse response = userService.findUserById(fakeUser.getId());

            // ------------------ [THEN] ------------------
            assertThat(response.getUsername()).isEqualTo("testUser");
        }

    }

    @Nested
    @DisplayName("실패 케이스")
    class Failure {
    }
}
