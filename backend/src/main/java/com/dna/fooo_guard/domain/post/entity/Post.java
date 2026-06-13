package com.dna.fooo_guard.domain.post.entity;

import com.dna.fooo_guard.domain.post.dto.PostEditRequest;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 생성자
@Table(name = "post", comment = "게시물 정보 테이블")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "게시물 고유 식별자(PK)")
    private Long id;

    @Column(name = "title", nullable = false, length = 20, comment = "게시물 제목")
    private String title;

    @Column(name = "content", nullable = false, length = 500, comment = "게시물 내용")
    private String content;

    @Column(name = "image_url", length = 500, comment = "이미지 파일 저장 경로 (S3 URL 등)")
    private String imageURL;

    @Column(name = "image_filename", length = 255, comment = "이미지 파일명")
    private String imageFilename;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false, comment = "게시물 유형 (FREE: 자유 게시물, DONATION: 기부 게시물)")
    private PostType postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "해당 게시물의 작성자 ID (성능을 위해 물리 FK는 제거)")
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.postType == null) {
            this.postType = PostType.FREE;
        }
    }

    public void edit(PostEditRequest dto) {
        this.title = CommonUtil.updateIfPresent(this.title, dto.getTitle());
        this.content = CommonUtil.updateIfPresent(this.content, dto.getContent());
        // TODO: 이미지 수정
    }
}
