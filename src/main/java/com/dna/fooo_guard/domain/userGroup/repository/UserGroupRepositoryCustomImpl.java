package com.dna.fooo_guard.domain.userGroup.repository;

import static com.dna.fooo_guard.domain.userGroup.entity.QUserGroup.userGroup;

import java.util.List;

import com.dna.fooo_guard.domain.userGroup.entity.UserGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserGroupRepositoryCustomImpl implements UserGroupRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserGroup> findAllByUserIdWithGroup(Long userId) {
        return queryFactory
                .selectFrom(userGroup)
                .join(userGroup.group).fetchJoin()
                .where(userGroup.user.id.eq(userId))
                .fetch();
    }
}