package com.dna.fooo_guard.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dna.fooo_guard.domain.group.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

}
