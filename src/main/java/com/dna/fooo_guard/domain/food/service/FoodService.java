package com.dna.fooo_guard.domain.food.service;

import org.springframework.stereotype.Service;

import com.dna.fooo_guard.domain.food.dto.FoodCreateRequest;
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

    public void createFood(FoodCreateRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Food newFood = dto.toEntity(user);
        foodRepository.save(newFood);
    }

    public FoodResponse findFoodByIdAndUserId(Long foodId, Long userId) {
        Food food = foodRepository.findByIdAndUserId(foodId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOOD_NOT_FOUND));
        return FoodResponse.from(food);
    }

}
