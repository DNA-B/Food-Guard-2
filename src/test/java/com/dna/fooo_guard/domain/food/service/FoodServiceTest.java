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

        // ------------------ [HELPERS] ------------------

        private static User createUser(Long userId) {
                return User.builder()
                                .id(userId)
                                .username("testUser")
                                .build();
        }

        private static Group createGroup(Long groupId) {
                return Group.builder()
                                .id(groupId)
                                .name("testGroup")
                                .build();
        }

        private static FoodCreateRequest createFoodCreateRequest(Long groupId) {
                return FoodCreateRequest.builder()
                                .name("포테이토 피자")
                                .type("피자")
                                .description("맛있는 포테이토 피자")
                                .expiryAt(LocalDate.of(2026, 12, 31))
                                .groupId(groupId)
                                .build();
        }

        private static FoodEditRequest createFoodEditRequest(Long groupId) {
                return FoodEditRequest.builder()
                                .name("바나나")
                                .type("과일")
                                .description("아침 바나나")
                                .expiryAt(LocalDate.of(2026, 11, 30))
                                .groupId(groupId)
                                .build();
        }

        private static Food createFood(Long foodId, String name, String type, String description, LocalDate expiryAt,
                        User user, Group group, FoodStatus status) {
                return Food.builder()
                                .id(foodId)
                                .name(name)
                                .type(type)
                                .description(description)
                                .expiryAt(expiryAt)
                                .user(user)
                                .group(group)
                                .status(status)
                                .build();
        }

        @Nested
        @DisplayName("성공 케이스")
        class Success {

                @Test
                @DisplayName("음식 생성 성공 - 그룹 없음")
                void createFood_Success_WithoutGroup() {
                        // ------------------ [GIVEN] ------------------
                        Long userId = 1L;
                        User fakeUser = createUser(userId);

                        FoodCreateRequest request = createFoodCreateRequest(null);

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

                        User fakeUser = createUser(userId);
                        Group fakeGroup = createGroup(groupId);

                        FoodCreateRequest request = createFoodCreateRequest(groupId);

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
                        User fakeUser = createUser(userId);

                        Food food1 = createFood(100L, "사과", "과일", "아침 사과", LocalDate.of(2026, 12, 31), fakeUser,
                                        null, null);

                        Food food2 = createFood(200L, "우유", "유제품", "신선한 우유", LocalDate.of(2026, 7, 10), fakeUser,
                                        null, null);

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
                        User fakeUser = createUser(userId);

                        Long foodId = 100L;
                        Food food1 = createFood(foodId, "사과", "과일", "아침 사과", LocalDate.of(2026, 12, 31), fakeUser,
                                        null, null);

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
                        User fakeUser = createUser(userId);

                        Long groupId = 1L;
                        Group fakeGroup = createGroup(groupId);

                        Long foodId = 100L;
                        Food originFood = createFood(foodId, "사과", "과일", "아침 사과", LocalDate.of(2026, 12, 31),
                                        fakeUser, null, null);

                        FoodEditRequest request = createFoodEditRequest(groupId);

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
                        User fakeUser = createUser(userId);

                        Long groupId = 1L;
                        Group fakeGroup = createGroup(groupId);
                        Group dummyGroup = Group.builder().id(-1L).build();

                        Long foodId = 100L;
                        Food originFood = createFood(foodId, "사과", "과일", "아침 사과", LocalDate.of(2026, 12, 31),
                                        fakeUser, fakeGroup, null);

                        FoodEditRequest request = createFoodEditRequest(-1L);

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
                        User fakeUser = createUser(userId);

                        Long foodId = 100L;
                        Food fakeFood = createFood(foodId, null, null, null, null, fakeUser, null, null);

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
                        User fakeUser = createUser(userId);

                        Long foodId = 100L;
                        Food food1 = createFood(foodId, "사과", "과일", "아침 사과", LocalDate.of(2026, 12, 31), fakeUser,
                                        null, null);

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
                        User fakeUser = createUser(userId);

                        Long foodId = 100L;
                        Food fakeFood = createFood(foodId, null, null, null, null, fakeUser, null, FoodStatus.DONATED);

                        given(foodRepository.findById(foodId)).willReturn(Optional.of(fakeFood));

                        // ------------------ [WHEN & THEN] ------------------
                        Throwable thrown = catchThrowable(() -> foodService.deleteFood(foodId, userId));

                        verify(foodRepository).findById(foodId);

                        assertThat(thrown)
                                        .isInstanceOf(CustomException.class)
                                        .satisfies(exception -> {
                                                CustomException customEx = (CustomException) exception;
                                                assertThat(customEx.getErrorCode())
                                                                .isEqualTo(ErrorCode.CANNOT_DELETE_DONATED_FOOD);
                                        });
                }
        }
}