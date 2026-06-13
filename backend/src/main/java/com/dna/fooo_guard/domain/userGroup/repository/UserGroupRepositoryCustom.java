package com.dna.fooo_guard.domain.userGroup.repository;

import java.util.List;

import com.dna.fooo_guard.domain.userGroup.entity.UserGroup;

public interface UserGroupRepositoryCustom {
    List<UserGroup> findAllByUserIdWithGroup(Long userId);
}
