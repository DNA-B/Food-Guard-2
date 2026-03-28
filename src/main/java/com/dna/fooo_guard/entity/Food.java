package com.dna.fooo_guard.entity;

import java.time.LocalDate;

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
import jakarta.persistence.Table;
import lombok.Getter;

enum FoodStatus {
  AVAILABLE,
  CONSUMED,
  DONATED,
}

@Entity
@Table(name = "food")
@Getter
public class Food extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(comment = "음식 고유 식별자(PK)")
    private Long id;

    @Column(nullable = false, comment = "음식 이름")
    private String name;

    @Column(comment = "음식 카테고리 (라면, 김밥 등)")
    private String type; 

    @Column(comment = "음식 설명")
    private String description;

    @Column(nullable = false, comment = "유통기한 또는 소비기한")
    private LocalDate expiryAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id", 
        nullable = false,
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
        comment = "해당 음식의 소유 사용자 ID (성능을 위해 물리 FK는 제거)"
    )
    private User user; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, comment = "음식 상태 (AVAILABLE: 이용가능, CONSUMED: 소비완료, DONATED: 나눔완료)")
    private FoodStatus status;

    @Column(comment = "이미지 파일 저장 경로 (S3 URL 등)")
    private String imageURL;

    @Column(comment = "이미지 파일명")
    private String imageFilename;
}
