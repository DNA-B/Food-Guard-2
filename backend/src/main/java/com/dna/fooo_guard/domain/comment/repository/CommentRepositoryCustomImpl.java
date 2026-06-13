package com.dna.fooo_guard.domain.comment.repository;

import static com.dna.fooo_guard.domain.comment.entity.QComment.comment;

import java.util.List;

import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findAllByPostIdWithParent(Long postId) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.user).fetchJoin()
                .where(comment.post.id.eq(postId))
                .orderBy(comment.createdAt.asc())
                .fetch();
    }

}
