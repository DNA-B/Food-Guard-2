package com.dna.fooo_guard.domain.post.repository;

import java.util.List;
import java.util.Optional;

import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.post.entity.QPost;
import com.dna.fooo_guard.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QPost post = QPost.post;
    private final QUser user = QUser.user;

    @Override
    public Optional<Post> findByIdWithUser(Long postId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(post)
                .join(post.user).fetchJoin()
                .where(post.id.eq(postId))
                .fetchOne());
    }

    @Override
    public List<Post> findAllWithUser() {
        return queryFactory
                .selectFrom(post)
                .join(post.user).fetchJoin()
                .fetch();
    }
}