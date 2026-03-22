package com.dna.fooo_guard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@Table(name = "users", comment = "사용자 정보 테이블")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "사용자 고유 고유 PK (Auto Increment)")
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50, unique = true, comment = "사용자 아이디 (로그인 시 사용)")
    private String userId;
}