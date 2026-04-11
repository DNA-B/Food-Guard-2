package com.dna.fooo_guard.domain.user.entity;

import com.dna.fooo_guard.domain.group.Group;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더가 내부적으로 쓸 생성자
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 객체 생성할 때 쓸 생성자
@Table(name = "users", comment = "사용자 정보 테이블")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "사용자 PK (Auto Increment)")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50, comment = "사용자 아이디 (로그인 시 사용)")
    private String username;

    @Column(name = "password", nullable = false, comment = "사용자 비밀번호 (암호화 저장)")
    private String password;

    @Column(name = "nickname", nullable = false, unique = true, length = 50, comment = "사용자 닉네임 (서비스 내에서 표시되는 이름)")
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "해당 사용자가 속한 그룹 ID (성능을 위해 물리 FK는 제거)")
    private Group group;
}