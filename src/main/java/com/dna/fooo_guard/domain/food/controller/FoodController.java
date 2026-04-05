package com.dna.fooo_guard.domain.food.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.food.dto.FoodCreateRequest;
import com.dna.fooo_guard.domain.food.dto.FoodEditRequest;
import com.dna.fooo_guard.domain.food.dto.FoodResponse;
import com.dna.fooo_guard.domain.food.service.FoodService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @PostMapping
    public ResponseEntity<Void> createFood(@RequestBody FoodCreateRequest dto, @AuthenticationPrincipal Long userId) {
        foodService.createFood(dto, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFood(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(foodService.findFoodByIdAndUserId(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editFood(@PathVariable Long id, @AuthenticationPrincipal Long userId,
            @RequestBody FoodEditRequest dto) {
        foodService.editFood(id, userId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        foodService.deleteFood(id, userId);
        return ResponseEntity.ok().build();
    }
}
