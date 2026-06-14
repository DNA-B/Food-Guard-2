package com.dna.fooo_guard.domain.food.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FoodRepositoryCustomImpl implements FoodRepositoryCustom {
    private final JPAQueryFactory queryFactory;

}