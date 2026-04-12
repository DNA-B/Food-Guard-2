package com.dna.fooo_guard.domain.userGroup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dna.fooo_guard.domain.userGroup.entity.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findAllByUserId(Long userId);

}
