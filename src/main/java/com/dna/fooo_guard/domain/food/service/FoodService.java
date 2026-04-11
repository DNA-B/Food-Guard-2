package com.dna.fooo_guard.domain.food.service;

import org.springframework.stereotype.Service;

import com.dna.fooo_guard.domain.food.dto.FoodCreateRequest;
import com.dna.fooo_guard.domain.food.dto.FoodEditRequest;
import com.dna.fooo_guard.domain.food.dto.FoodResponse;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodService {
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    private Food getFoodWithAccessCheck(Long foodId, Long userId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOOD_NOT_FOUND));

        if (!food.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        return food;
    }

    public void createFood(FoodCreateRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Food newFood = dto.toEntity(user);
        foodRepository.save(newFood);
    }

    public FoodResponse findFoodByIdAndUserId(Long foodId, Long userId) {
        Food food = getFoodWithAccessCheck(foodId, userId);
        return FoodResponse.from(food);
    }

    // dirtyCheking으로 DB 자동 반영하기
    public void editFood(Long foodId, Long userId, FoodEditRequest dto) {
        Food food = getFoodWithAccessCheck(foodId, userId);
        food.edit(dto);
    }

    public void deleteFood(Long foodId, Long userId) {
        Food food = getFoodWithAccessCheck(foodId, userId);
        foodRepository.delete(food);
    }
}
