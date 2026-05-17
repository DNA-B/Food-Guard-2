package com.dna.fooo_guard.domain.comment.entity;

import com.dna.fooo_guard.domain.comment.dto.CommentEditRequest;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.global.BaseEntity;
import com.dna.fooo_guard.global.util.CommonUtil;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더가 내부적으로 쓸 생성자
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 객체 생성할 때 쓸 생성자
@Table(name = "comments", comment = "댓글 테이블")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "댓글 고유 식별자(PK)")
    private Long id;

    @Column(name = "content", nullable = false, length = 500, comment = "댓글 내용")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "댓글이 달린 게시글 ID")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "부모 댓글 ID")
    private Comment parent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20, comment = "댓글 상태 (PUBLISHED: 게시됨, EDITED: 수정됨, DELETED: 삭제됨)")
    private CommentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "해당 댓글의 작성자 ID (성능을 위해 물리 FK는 제거)")
    private User user;

    @PrePersist
    private void prePersist() {
        status = CommentStatus.PUBLISHED;
    }

    public void edit(CommentEditRequest dto) {
        this.content = CommonUtil.updateIfPresent(this.content, dto.getContent());
        this.status = CommentStatus.EDITED;
    }

    public void delete() {
        this.status = CommentStatus.DELETED;
    }
}
