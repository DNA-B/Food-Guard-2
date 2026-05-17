package com.dna.fooo_guard.domain.chat.entity;

import java.time.LocalDateTime;

import com.dna.fooo_guard.domain.donation.entity.Donation;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room", comment = "채팅방 정보 테이블", uniqueConstraints = {
        @UniqueConstraint(name = "uk_chat_room_donation_requester", columnNames = { "donation_id", "requester_id" })
})
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, comment = "채팅방 PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "채팅 대상 나눔 ID")
    private Donation donation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "나눔글 작성자 ID")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "나눔 문의자 ID")
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20, comment = "채팅방 상태 (OPEN: 진행중, CLOSED: 종료)")
    private ChatRoomStatus status;

    @Column(name = "last_message", length = 500, comment = "마지막 메시지 내용")
    private String lastMessage;

    @Column(name = "last_message_at", comment = "마지막 메시지 발송 일시")
    private LocalDateTime lastMessageAt;

    @PrePersist
    private void prePersist() {
        if (this.status == null) {
            this.status = ChatRoomStatus.OPEN;
        }
    }

    public void updateLastMessage(String content, LocalDateTime sentAt) {
        this.lastMessage = content;
        this.lastMessageAt = sentAt;
    }

    public void close() {
        this.status = ChatRoomStatus.CLOSED;
    }
}
