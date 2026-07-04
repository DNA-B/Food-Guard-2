package com.dna.fooo_guard.domain.post.repository;

import java.util.List;
import java.util.Optional;

import com.dna.fooo_guard.domain.post.entity.Post;

public interface PostRepositoryCustom {
    Optional<Post> findByIdWithUser(Long postId);

    List<Post> findAllWithUser();
}
