package com.dna.fooo_guard.domain.food.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.dna.fooo_guard.domain.food.dto.FoodCreateRequest;
import com.dna.fooo_guard.domain.food.dto.FoodEditRequest;
import com.dna.fooo_guard.domain.food.dto.FoodResponse;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private FoodRepository foodRepository;
    @InjectMocks
    private FoodService foodService;

    private User testUser;
    private Food testFood;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("테스트유저")
                .build();

        testFood = Food.builder()
                .id(1L)
                .name("기존음식")
                .type("냉동")
                .user(testUser)
                .build();
    }

    @Test
    @DisplayName("음식 생성 성공")
    void testCreateFood() {
        FoodCreateRequest dto = FoodCreateRequest.builder()
                .name("새로운음식")
                .type("냉장")
                .description("맛있는 음식")
                .expiryAt(null)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        foodService.createFood(dto, 1L);

        verify(userRepository).findById(1L);
        verify(foodRepository).save(any(Food.class));
    }

    @Test
    @DisplayName("음식 수정 성공 - 엔티티 edit 메서드 호출 확인")
    void testEditFood() {
        FoodEditRequest editDto = FoodEditRequest.builder()
                .name("수정된음식")
                .type(null)
                .build();

        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));

        foodService.editFood(1L, 1L, editDto);

        assertEquals("수정된음식", testFood.getName());
        assertEquals("냉동", testFood.getType()); // null로 보낸 필드는 기존 값 유지
        verify(foodRepository).findById(1L);
    }

    @Test
    @DisplayName("음식 삭제 성공")
    void testDeleteFood() {
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        foodService.deleteFood(1L, 1L);
        verify(foodRepository).delete(testFood);
    }

    @Test
    @DisplayName("권한 오류 - 내 음식이 아닌 경우 수정 실패")
    void testEditFood_AccessDenied() {
        Long strangerId = 999L;
        FoodEditRequest editDto = FoodEditRequest.builder().name("해킹시도").build();

        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));

        CustomException exception = assertThrows(CustomException.class, () -> {
            foodService.editFood(1L, strangerId, editDto);
        });

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @Test
    @DisplayName("음식 조회(foodId, userId) 성공")
    void testFindFoodByIdAndUserId() {
        Long foodId = testFood.getId();
        Long userId = testUser.getId();

        when(foodRepository.findById(foodId)).thenReturn(Optional.of(testFood));

        FoodResponse response = foodService.findFoodByIdAndUserId(foodId, userId);

        assertNotNull(response);
        assertEquals(foodId, response.getId());
        assertEquals(testFood.getName(), response.getName());

        verify(foodRepository, times(1)).findById(foodId);
    }
}
