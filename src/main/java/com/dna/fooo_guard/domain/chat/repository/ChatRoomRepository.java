package com.dna.fooo_guard.domain.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dna.fooo_guard.domain.chat.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByDonationIdAndRequesterId(Long donationId, Long requesterId);

    List<ChatRoom> findAllByOwnerIdOrRequesterId(Long ownerId, Long requesterId);
}
