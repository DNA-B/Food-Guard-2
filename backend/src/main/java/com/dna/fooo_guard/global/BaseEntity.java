package com.dna.fooo_guard.global;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
	// @Column(name, { options } length, comment)

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false, comment = "생성 일시")
	protected LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false, comment = "수정 일시")
	protected LocalDateTime updatedAt;
}
