package com.dna.fooo_guard.domain.food.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    // Helper Function start
    private Food getFoodWithAccessCheck(Long foodId, Long userId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOOD_NOT_FOUND));

        if (!food.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return food;
    }

    private Group findGroupOrNull(Long groupId) {
        if (groupId == null) {
            return null;
        }

        return groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
    }
    // Helper Function end

    @Transactional
    public void createFood(FoodCreateRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Group group = findGroupOrNull(dto.getGroupId());
        Food newFood = dto.toEntity(user, group);

        foodRepository.save(newFood);
    }

    public FoodResponse findFoodByIdAndUserId(Long foodId, Long userId) {
        Food food = getFoodWithAccessCheck(foodId, userId);
        return FoodResponse.from(food);
    }

    // dirtyCheking으로 DB 자동 반영하기
    @Transactional
    public void editFood(Long foodId, Long userId, FoodEditRequest dto) {
        Food food = getFoodWithAccessCheck(foodId, userId);
        Group group = findGroupOrNull(dto.getGroupId());
        food.edit(dto, group); // group이 null이면 그대로 null로 수정
    }

    @Transactional
    public void deleteFood(Long foodId, Long userId) {
        Food food = getFoodWithAccessCheck(foodId, userId);
        foodRepository.delete(food);
    }
}
