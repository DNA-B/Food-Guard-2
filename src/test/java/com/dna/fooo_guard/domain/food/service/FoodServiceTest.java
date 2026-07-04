package com.dna.fooo_guard.domain.food.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor; 
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dna.fooo_guard.domain.food.dto.FoodCreateRequest;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.group.entity.Group;
import com.dna.fooo_guard.domain.group.repository.GroupRepository;
import com.dna.fooo_guard.domain.user.entity.User;
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
            verify(groupRepository, never()).getReferenceById(null); // groupId 없으면 실행 X

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
    }
    
    @Nested
    @DisplayName("실패 케이스")
    class Failure { 
        
    }
}