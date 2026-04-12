package com.dna.fooo_guard.domain.userGroup.entity;

import com.dna.fooo_guard.domain.group.entity.Group;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.global.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 객체 생성할 때 쓸 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더가 내부적으로 쓸 생성자
@Table(name = "user_groups", comment = "사용자-그룹 매핑 테이블")
public class UserGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "사용자-그룹 고유 식별자(PK)")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "소속 그룹 PK")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "그룹에 속한 사용자 PK")
    private User user;
}