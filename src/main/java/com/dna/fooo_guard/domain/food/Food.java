package com.dna.fooo_guard.domain.food;

import java.time.LocalDate;

import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.global.BaseEntity;

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
import lombok.Getter;

enum FoodStatus {
    AVAILABLE,
    CONSUMED,
    DONATED,
}

@Entity
@Table(name = "food", comment = "음식 정보 테이블")
@Getter
public class Food extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "음식 고유 식별자(PK)")
    private Long id;

    @Column(name = "name", nullable = false, length = 20, comment = "음식 이름")
    private String name;

    @Column(name = "type", length = 50, comment = "음식 카테고리 (라면, 김밥 등)")
    private String type;

    @Column(name = "description", length = 255, comment = "음식 설명")
    private String description;

    @Column(name = "expiry_at", comment = "유통기한 또는 소비기한")
    private LocalDate expiryAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20, comment = "음식 상태 (AVAILABLE: 이용가능, CONSUMED: 소비완료, DONATED: 나눔완료)")
    private FoodStatus status;

    @Column(name = "image_url", length = 500, comment = "이미지 파일 저장 경로 (S3 URL 등)")
    private String imageURL;

    @Column(name = "image_filename", length = 255, comment = "이미지 파일명")
    private String imageFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "해당 음식의 소유자 ID (성능을 위해 물리 FK는 제거)")
    private User user;

    @PrePersist
    public void initializeStatus() {
        this.status = FoodStatus.AVAILABLE; // 기본 상태 설정
    }
}
