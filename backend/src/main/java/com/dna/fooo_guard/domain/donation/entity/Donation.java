package com.dna.fooo_guard.domain.donation.entity;

import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.global.BaseEntity;
import com.dna.fooo_guard.global.util.CommonUtil; // 공통 유틸 임포트
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "donation", comment = "음식 나눔 상세 정보 테이블")
public class Donation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "나눔 상세 고유 식별자(PK)")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "연관된 공통 게시물 ID")
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "나눔할 대상 음식 ID")
    private Food food;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, comment = "나눔 진행 상태 (ONGOING: 나눔중, COMPLETED: 나눔완료)")
    private DonationStatus status;

    @PrePersist
    private void prePersist() {
        if (this.status == null) {
            this.status = DonationStatus.ONGOING;
        }
    }

    public void edit(Food food) {
        this.food = CommonUtil.updateIfPresent(this.food, food);
    }

    public void updateStatus(DonationStatus status) {
        this.status = CommonUtil.updateIfPresent(this.status, status);
    }
}