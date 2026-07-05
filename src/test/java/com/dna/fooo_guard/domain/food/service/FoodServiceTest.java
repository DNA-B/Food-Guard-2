package com.dna.fooo_guard.domain.food.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dna.fooo_guard.domain.food.dto.FoodCreateRequest;
import com.dna.fooo_guard.domain.food.dto.FoodEditRequest;
import com.dna.fooo_guard.domain.food.dto.FoodResponse;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.food.entity.FoodStatus;
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
                @DisplayName("음식 생성 성공 - 그룹 없음")
                void createFood_Success_WithoutGroup() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = User.builder().id(userId).username("testUser").build();

                        FoodCreateRequest request = FoodCreateRequest.builder()
                                        .name("포테이토 피자")
                                        .type("피자")
                                        .description("맛있는 포테이토 피자")
                                        .expiryAt(LocalDate.of(2026, 12, 31))
                                        .groupId(null)
                                        .build();

                        given(userRepository.getReferenceById(userId)).willReturn(fakeUser);

                        // ------------------ [WHEN] ------------------
                        foodService.createFood(request, userId);

                        // ------------------ [THEN] ------------------
                        verify(userRepository).getReferenceById(userId);
                        verify(groupRepository, never()).getReferenceById(null);

                        ArgumentCaptor<Food> foodCaptor = ArgumentCaptor.forClass(Food.class);
                        verify(foodRepository).save(foodCaptor.capture());
                        Food savedFood = foodCaptor.getValue();

                        assertThat(savedFood)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("name", "type", "description", "expiryAt")
                                        .isEqualTo(request);

                        assertThat(savedFood.getUser().getId()).isEqualTo(userId);
                        assertThat(savedFood.getGroup()).isNull();
                }

                @Test
                @DisplayName("음식 생성 성공 - 그룹 포함")
                void createFood_Success_WithGroup() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        Long groupId = 10L;

                        User fakeUser = User.builder().id(userId).username("testUser").build();
                        Group fakeGroup = Group.builder().id(groupId).name("testGroup").build();

                        FoodCreateRequest request = FoodCreateRequest.builder()
                                        .name("포테이토 피자")
                                        .type("피자")
                                        .description("맛있는 포테이토 피자")
                                        .expiryAt(LocalDate.of(2026, 12, 31))
                                        .groupId(groupId)
                                        .build();

                        given(userRepository.getReferenceById(userId)).willReturn(fakeUser);
                        given(groupRepository.getReferenceById(groupId)).willReturn(fakeGroup);

                        // ------------------ [WHEN] ------------------
                        foodService.createFood(request, userId);

                        // ------------------ [THEN] ------------------
                        verify(userRepository).getReferenceById(userId);
                        verify(groupRepository).getReferenceById(groupId);

                        ArgumentCaptor<Food> foodCaptor = ArgumentCaptor.forClass(Food.class);
                        verify(foodRepository).save(foodCaptor.capture());
                        Food savedFood = foodCaptor.getValue();

                        assertThat(savedFood)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("name", "type", "description", "expiryAt")
                                        .isEqualTo(request);

                        assertThat(savedFood.getUser().getId()).isEqualTo(userId);
                        assertThat(savedFood.getGroup().getId()).isEqualTo(groupId);
                }

                @Test
                @DisplayName("음식 전체 조회 성공")
                void findAllFoodByUserId_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = User.builder().id(userId).username("testUser").build();

                        Food food1 = Food.builder()
                                        .id(100L)
                                        .name("사과")
                                        .type("과일")
                                        .description("아침 사과")
                                        .expiryAt(LocalDate.of(2026, 12, 31))
                                        .user(fakeUser)
                                        .build();

                        Food food2 = Food.builder()
                                        .id(200L)
                                        .name("우유")
                                        .type("유제품")
                                        .description("신선한 우유")
                                        .expiryAt(LocalDate.of(2026, 7, 10))
                                        .user(fakeUser)
                                        .build();

                        List<Food> fakeFoods = List.of(food1, food2);

                        given(foodRepository.findAllByUserId(userId)).willReturn(fakeFoods);

                        // ------------------ [WHEN] ------------------
                        List<FoodResponse> responses = foodService.findAllFoodByUserId(userId);

                        // ------------------ [THEN] ------------------
                        verify(foodRepository).findAllByUserId(userId);

                        assertThat(responses).hasSize(2);
                        assertThat(responses)
                                        .extracting("name", "type", "description", "expiryAt")
                                        .containsExactly(
                                                        tuple("사과", "과일", "아침 사과", LocalDate.of(2026, 12, 31)),
                                                        tuple("우유", "유제품", "신선한 우유", LocalDate.of(2026, 7, 10)));
                }

                @Test
                @DisplayName("음식 1건 조회 성공")
                void findFoodById_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = User.builder().id(userId).username("testUser").build();

                        Long foodId = 100L;
                        Food food1 = Food.builder()
                                        .id(foodId)
                                        .name("사과")
                                        .type("과일")
                                        .description("아침 사과")
                                        .expiryAt(LocalDate.of(2026, 12, 31))
                                        .user(fakeUser)
                                        .build();

                        given(foodRepository.findById(foodId)).willReturn(Optional.of(food1));

                        // ------------------ [WHEN] ------------------
                        FoodResponse response = foodService.findFoodByIdAndUserId(foodId, userId);

                        // ------------------ [THEN] ------------------
                        verify(foodRepository).findById(foodId);

                        assertThat(response)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("name", "type", "description", "expiryAt")
                                        .isEqualTo(food1);
                }

                @Test
                @DisplayName("음식 수정 성공 - 그룹 변경")
                void editFood_Success_GroupChanged() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = User.builder().id(userId).build();

                        Long groupId = 1L;
                        Group fakeGroup = Group.builder().id(groupId).name("testGroup").build();

                        Long foodId = 100L;
                        Food originFood = Food.builder()
                                        .id(foodId)
                                        .name("사과")
                                        .type("과일")
                                        .description("아침 사과")
                                        .expiryAt(LocalDate.of(2026, 12, 31))
                                        .user(fakeUser)
                                        .group(null)
                                        .build();

                        FoodEditRequest request = FoodEditRequest.builder()
                                        .name("바나나")
                                        .type("과일")
                                        .description("아침 바나나")
                                        .expiryAt(LocalDate.of(2026, 11, 30))
                                        .groupId(groupId)
                                        .build();

                        given(foodRepository.findById(foodId)).willReturn(Optional.of(originFood));
                        given(groupRepository.getReferenceById(groupId)).willReturn(fakeGroup);

                        // ------------------ [WHEN] ------------------
                        foodService.editFood(foodId, userId, request);

                        // ------------------ [THEN] ------------------
                        verify(foodRepository).findById(foodId);
                        verify(groupRepository).getReferenceById(groupId);

                        assertThat(originFood)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("name", "type", "description", "expiryAt")
                                        .isEqualTo(request);

                        assertThat(originFood.getGroup()).isEqualTo(fakeGroup);
                }

                @Test
                @DisplayName("음식 수정 성공 - 그룹 해제(-1)")
                void editFood_Success_ClearGroup() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = User.builder().id(userId).build();

                        Long groupId = 1L;
                        Group fakeGroup = Group.builder().id(groupId).name("testGroup").build();
                        Group dummyGroup = Group.builder().id(-1L).build();

                        Long foodId = 100L;
                        Food originFood = Food.builder()
                                        .id(foodId)
                                        .name("사과")
                                        .type("과일")
                                        .description("아침 사과")
                                        .expiryAt(LocalDate.of(2026, 12, 31))
                                        .user(fakeUser)
                                        .group(fakeGroup) 
                                        .build();

                        FoodEditRequest request = FoodEditRequest.builder()
                                        .name("바나나")
                                        .type("과일")
                                        .description("아침 바나나")
                                        .expiryAt(LocalDate.of(2026, 11, 30))
                                        .groupId(-1L)
                                        .build();

                        given(foodRepository.findById(foodId)).willReturn(Optional.of(originFood));
                        given(groupRepository.getReferenceById(-1L)).willReturn(dummyGroup);

                        // ------------------ [WHEN] ------------------
                        foodService.editFood(foodId, userId, request);

                        // ------------------ [THEN] ------------------
                        verify(foodRepository).findById(foodId);
                        verify(groupRepository).getReferenceById(-1L);

                        assertThat(originFood)
                                        .usingRecursiveComparison()
                                        .comparingOnlyFields("name", "type", "description", "expiryAt")
                                        .isEqualTo(request);

                        assertThat(originFood.getGroup()).isNull(); 
                }
                
                @Test
                @DisplayName("음식 삭제 성공")
                void deleteFood_Success() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = User.builder().id(userId).build(); 

                        Long foodId = 100L;
                        Food fakeFood = Food.builder().id(foodId).user(fakeUser).build(); 

                        given(foodRepository.findById(foodId)).willReturn(Optional.of(fakeFood));

                        // ------------------ [WHEN] ------------------
                        foodService.deleteFood(foodId, userId);

                        // ------------------ [THEN] ------------------
                        verify(foodRepository).findById(foodId);
                        verify(foodRepository).delete(fakeFood);
                }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

                @Test
                @DisplayName("음식 1건 조회 실패 - 음식 없음")
                void findFoodById_Fail_FoodNotFound() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        Long wrongFoodId = 100L;

                        given(foodRepository.findById(wrongFoodId)).willReturn(Optional.empty());

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> foodService.findFoodByIdAndUserId(wrongFoodId, userId));

                        verify(foodRepository).findById(wrongFoodId);

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.FOOD_NOT_FOUND);
                                        });
                }

                @Test
                @DisplayName("음식 1건 조회 실패 - 권한 없음")
                void findFoodById_Fail_AccessDenied() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        Long wrongUserId = 2L;
                        User fakeUser = User.builder().id(userId).username("testUser").build();

                        Long foodId = 100L;
                        Food food1 = Food.builder()
                                        .id(foodId)
                                        .name("사과")
                                        .type("과일")
                                        .description("아침 사과")
                                        .expiryAt(LocalDate.of(2026, 12, 31))
                                        .user(fakeUser)
                                        .build();

                        given(foodRepository.findById(foodId)).willReturn(Optional.of(food1));

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> foodService.findFoodByIdAndUserId(foodId, wrongUserId));

                        verify(foodRepository).findById(foodId);

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED);
                                        });
                }

                @Test
                @DisplayName("음식 삭제 실패 - 기부된 음식 삭제 불가")
                void deleteFood_Fail_CannotDeleteDonatedFood() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = User.builder().id(userId).build();

                        Long foodId = 100L;
                        Food fakeFood = Food.builder()
                                        .id(foodId)
                                        .user(fakeUser)
                                        .status(FoodStatus.DONATED)
                                        .build();

                        given(foodRepository.findById(foodId)).willReturn(Optional.of(fakeFood));

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> foodService.deleteFood(foodId, userId));

                        verify(foodRepository).findById(foodId);

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode()).isEqualTo(ErrorCode.CANNOT_DELETE_DONATED_FOOD);
                                        });
                }
        }
}