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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", comment = "사용자 정보 테이블")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "사용자 PK (Auto Increment)")
    private Long id;

    @Column(name = "username", nullable = false, length = 50, unique = true, comment = "사용자 아이디 (로그인 시 사용)")
    private String username;

    @Column(name = "password", nullable = false, comment = "사용자 비밀번호 (암호화 저장)")
    private String password;

    @Column(name = "nickname", nullable = false, length = 50, unique = true, comment = "사용자 닉네임 (서비스 내에서 표시되는 이름)")
    private String nickname;
}