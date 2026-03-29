package com.dna.fooo_guard.global;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseEntity {
	// @Column(name, { options } length, comment)

	@Column(name = "created_at", nullable = false, updatable = false, comment = "생성 일시")
	private LocalDateTime createdAt;
	@Column(name = "updated_at", nullable = false, comment = "수정 일시")
	private LocalDateTime updatedAt;

	@PrePersist
	public void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
