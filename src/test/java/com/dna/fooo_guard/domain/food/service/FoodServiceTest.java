package com.dna.fooo_guard.domain.food.service;

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

import com.dna.fooo_guard.domain.food.dto.FoodCreateRequest;
import com.dna.fooo_guard.domain.food.dto.FoodEditRequest;
import com.dna.fooo_guard.domain.food.dto.FoodResponse;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.group.entity.Group;
import com.dna.fooo_guard.domain.group.repository.GroupRepository;
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
    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private FoodService foodService;

    private User testUser;
    private Food testFood;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("테스트유저").build();
        testFood = Food.builder().id(1L).name("기존음식").type("냉동").user(testUser).build();
    }

    @Test
    @DisplayName("성공 - 그룹 포함 음식 생성")
    void testCreateFood_WithGroup() {
        Long groupId = 10L;
        FoodCreateRequest dto = FoodCreateRequest.builder()
                .name("그룹음식")
                .type("김밥")
                .groupId(groupId)
                .build();
        Group testGroup = Group.builder().id(groupId).name("공유냉장고").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));

        foodService.createFood(dto, 1L);

        verify(userRepository).findById(1L);
        verify(groupRepository).findById(groupId);
        verify(foodRepository).save(any(Food.class));
    }

    @Test
    @DisplayName("성공 - 그룹 없이 음식 생성")
    void testCreateFood_WithoutGroup() {
        FoodCreateRequest dto = FoodCreateRequest.builder()
                .name("단품음식")
                .type("과일")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        foodService.createFood(dto, 1L);

        verify(userRepository).findById(1L);
        verify(groupRepository, never()).findById(any());
        verify(foodRepository).save(any(Food.class));
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 사용자 ID인 경우 USER_NOT_FOUND 발생")
    void testCreateFood_UserNotFound() {
        FoodCreateRequest dto = FoodCreateRequest.builder().name("음식").build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            foodService.createFood(dto, 1L);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    @DisplayName("성공 - ID와 사용자 ID로 음식 조회")
    void testFindFoodByIdAndUserId_Success() {
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));

        FoodResponse response = foodService.findFoodByIdAndUserId(1L, 1L);

        assertEquals(1L, response.getId());
        assertEquals("기존음식", response.getName());
        assertEquals("냉동", response.getType());
    }

    @Test
    @DisplayName("성공 - 그룹 정보 변경을 포함한 음식 수정")
    void testEditFood() {
        Long newGroupId = 20L;
        Group newGroup = Group.builder().id(newGroupId).name("새그룹").build();
        FoodEditRequest editDto = FoodEditRequest.builder()
                .name("수정음식")
                .groupId(newGroupId)
                .build();

        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        when(groupRepository.findById(newGroupId)).thenReturn(Optional.of(newGroup));

        foodService.editFood(1L, 1L, editDto);

        assertEquals("수정음식", testFood.getName());
        assertEquals(newGroupId, testFood.getGroup().getId());
    }

    @Test
    @DisplayName("실패 - 수정 시 존재하지 않는 그룹 ID인 경우 GROUP_NOT_FOUND 발생")
    void testEditFood_GroupNotFound() {
        Long invalidGroupId = 999L;
        FoodEditRequest editDto = FoodEditRequest.builder().groupId(invalidGroupId).build();

        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        when(groupRepository.findById(invalidGroupId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            foodService.editFood(1L, 1L, editDto);
        });

        assertEquals(ErrorCode.GROUP_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패 - 음식을 찾을 수 없는 경우 FOOD_NOT_FOUND 발생")
    void testEditFood_FoodNotFound() {
        Long invalidFoodId = 888L;
        FoodEditRequest editDto = FoodEditRequest.builder().name("수정").build();

        when(foodRepository.findById(invalidFoodId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            foodService.editFood(invalidFoodId, 1L, editDto);
        });

        assertEquals(ErrorCode.FOOD_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패 - 유저 권한이 없는 경우 ACCESS_DENIED 발생")
    void testEditFood_AccessDenied() {
        Long strangerId = 999L;
        FoodEditRequest editDto = FoodEditRequest.builder().name("해킹").build();

        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));

        CustomException exception = assertThrows(CustomException.class, () -> {
            foodService.editFood(1L, strangerId, editDto);
        });

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @Test
    @DisplayName("성공 - 음식 삭제")
    void testDeleteFood() {
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));

        foodService.deleteFood(1L, 1L);

        verify(foodRepository).delete(testFood);
    }

    @Test
    @DisplayName("실패 - 삭제 시 권한이 없는 경우 ACCESS_DENIED 발생")
    void testDeleteFood_AccessDenied() {
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));

        CustomException exception = assertThrows(CustomException.class, () -> {
            foodService.deleteFood(1L, 999L);
        });

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
        verify(foodRepository, never()).delete(any(Food.class));
    }
}
