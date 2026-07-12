package com.dna.fooo_guard.domain.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dna.fooo_guard.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByDonationIdAndGuestId(Long donationId, Long guestId);

    List<ChatRoom> findByHostIdOrGuestId(Long hostId, Long guestId);

    void deleteAllByDonationId(Long donationId);
}