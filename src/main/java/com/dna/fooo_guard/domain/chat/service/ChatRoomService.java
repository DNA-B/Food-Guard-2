package com.dna.fooo_guard.domain.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.chat.dto.ChatRoomResponse;
import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.repository.ChatRoomRepository;
import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.donation.repository.DonationRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoomResponse startChatRoom(Long donationId, Long requesterId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new CustomException(ErrorCode.DONATION_NOT_FOUND));
        Long ownerId = donation.getPost().getUser().getId();

        if (ownerId.equals(requesterId)) {
            throw new CustomException(ErrorCode.CANNOT_CHAT_WITH_SELF);
        }

        return chatRoomRepository.findByDonationIdAndRequesterId(donationId, requesterId)
                .map(chatRoom -> ChatRoomResponse.from(chatRoom, requesterId))
                .orElseGet(() -> createChatRoom(donation, requesterId));
    }

    public List<ChatRoomResponse> findMyChatRooms(Long userId) {
        return chatRoomRepository.findAllByOwnerIdOrRequesterId(userId, userId).stream()
                .map(chatRoom -> ChatRoomResponse.from(chatRoom, userId))
                .toList();
    }

    public ChatRoomResponse findMyChatRoom(Long id, Long userId) {
        ChatRoom chatRoom = getChatRoomWithAccessCheck(id, userId);
        return ChatRoomResponse.from(chatRoom, userId);
    }

    @Transactional
    public void closeChatRoom(Long id, Long userId) {
        ChatRoom chatRoom = getChatRoomWithAccessCheck(id, userId);
        chatRoom.close();
    }

    private ChatRoom getChatRoomWithAccessCheck(Long id, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.getOwner().getId().equals(userId) && !chatRoom.getRequester().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return chatRoom;
    }

    private ChatRoomResponse createChatRoom(Donation donation, Long requesterId) {
        User requester = userRepository.getReferenceById(requesterId);

        ChatRoom chatRoom = ChatRoom.builder()
                .donation(donation)
                .owner(donation.getPost().getUser())
                .requester(requester)
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponse.from(savedChatRoom, requesterId);
    }
}
