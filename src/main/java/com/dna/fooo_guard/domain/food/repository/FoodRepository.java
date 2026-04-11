package com.dna.fooo_guard.domain.food.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dna.fooo_guard.domain.food.entity.Food;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByIdAndUserId(Long foodId, Long userId);
}
