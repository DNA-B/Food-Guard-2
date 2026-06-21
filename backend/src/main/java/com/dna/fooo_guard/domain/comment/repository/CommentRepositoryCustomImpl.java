package com.dna.fooo_guard.domain.comment.repository;

import java.util.List;

import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.comment.entity.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;

    @Override
    public List<Comment> findAllByPostIdWithParent(Long postId) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.user).fetchJoin() // 작성자 hard delete니까 null 될 수 있음.
                .where(comment.post.id.eq(postId))
                .orderBy(comment.createdAt.asc())
                .fetch();
    }

}
