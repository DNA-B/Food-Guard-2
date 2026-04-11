package com.dna.fooo_guard.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dna.fooo_guard.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findAllByGroupId(Long groupId);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);
}
