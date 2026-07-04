package com.dna.fooo_guard.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dna.fooo_guard.domain.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findAllByUserId(Long userId);

    Optional<Post> findByIdAndUserId(Long postId, Long userId);

    Void deleteAllByUserId(Long userId);
}
