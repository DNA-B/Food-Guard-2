package com.dna.fooo_guard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "groups", comment = "공유 냉장고 그룹 테이블")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "그룹 고유 식별자(PK)")
    private Long id;

    @Column(name = "name", nullable = false, length = 20, comment = "그룹 이름")
    private String name;

    @Column(name = "manager", nullable = false, length = 20, comment = "그룹 관리자")
    private String manager;

    @Column(name = "description", length = 255, comment = "그룹 설명")
    private String description;
}
