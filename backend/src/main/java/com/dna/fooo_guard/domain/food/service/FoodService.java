package com.dna.fooo_guard.domain.food.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    // [권한 및 존재 검증 헬퍼]
    private Food getFoodWithAccessCheck(Long foodId, Long userId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOOD_NOT_FOUND));

        if (!food.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return food;
    }

    @Transactional
    public void createFood(FoodCreateRequest dto, Long userId) {
        User user = userRepository.getReferenceById(userId);

        Group group = null;
        if (dto.getGroupId() != null) {
            group = groupRepository.getReferenceById(dto.getGroupId());
        }

        Food newFood = dto.toEntity(user, group);
        foodRepository.save(newFood);
    }

    public List<FoodResponse> findAllFoodByUserId(Long userId) {
        List<Food> foods = foodRepository.findAllByUserId(userId);
        return foods.stream()
                .map(FoodResponse::from)
                .toList();
    }

    public FoodResponse findFoodByIdAndUserId(Long foodId, Long userId) {
        Food food = getFoodWithAccessCheck(foodId, userId);
        return FoodResponse.from(food);
    }

    @Transactional
    public void editFood(Long foodId, Long userId, FoodEditRequest dto) {
        Food food = getFoodWithAccessCheck(foodId, userId);
        Group targetGroup = groupRepository.getReferenceById(dto.getGroupId());

        // -1이나 null이어도 edit에서는 CommonUtil 덕분에 문제 없음.
        food.edit(dto, targetGroup);

        // 실제로 -1일 때는 group 해제
        if (dto.getGroupId() != null && dto.getGroupId() == -1) {
            food.clearGroup();
        }
    }

    @Transactional
    public void deleteFood(Long foodId, Long userId) {
        Food food = getFoodWithAccessCheck(foodId, userId);

        if (food.getStatus() == FoodStatus.DONATED) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_DONATED_FOOD);
        }

        foodRepository.delete(food);
    }
}