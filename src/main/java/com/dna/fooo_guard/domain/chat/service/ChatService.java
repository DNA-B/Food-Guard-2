package com.dna.fooo_guard.domain.chat.service;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageDto;
import com.dna.fooo_guard.domain.chat.dto.ChatRoomResponseDto;
import com.dna.fooo_guard.domain.chat.entity.ChatMessage;
import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.repository.ChatMessageRepository;
import com.dna.fooo_guard.domain.chat.repository.ChatRoomRepository;
import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.donation.repository.DonationRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

        private final ChatRoomRepository chatRoomRepository;
        private final DonationRepository donationRepository;
        private final UserRepository userRepository;
        private final ChatMessageRepository chatMessageRepository;

        @Transactional
        public Long createChatRoom(Long donationId, Long guestId) {
                Donation donation = donationRepository.findById(donationId)
                                .orElseThrow(() -> new CustomException(ErrorCode.DONATION_NOT_FOUND));

                User guest = userRepository.findById(guestId)
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                User host = donation.getPost().getUser();

                if (host.getId().equals(guestId)) {
                        throw new CustomException(ErrorCode.CANNOT_CHAT_WITH_SELF);
                }

                // ✨ [2번 규칙 보장]: donationId와 guestId 쌍으로 조회하므로,
                // 새로운 guestId(예: 3번 유저)가 들어오면 기존 1번 방을 타지 않고 반드시 orElseGet으로 새 방을 생성합니다[cite:
                // 4].
                return chatRoomRepository.findByDonationIdAndGuestId(donationId, guestId)
                                .map(room -> room.getId())
                                .orElseGet(() -> {
                                        ChatRoom newRoom = ChatRoom.builder()
                                                        .donation(donation)
                                                        .host(host)
                                                        .guest(guest)
                                                        .build();
                                        return chatRoomRepository.save(newRoom).getId();
                                });
        }

        @Transactional
        public ChatMessageDto processMessage(ChatMessageDto messageDto) {
                ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getRoomId())
                                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

                User sender = userRepository.findById(messageDto.getSenderId())
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                if (!chatRoom.getHost().getId().equals(sender.getId()) &&
                                !chatRoom.getGuest().getId().equals(sender.getId())) {
                        throw new CustomException(ErrorCode.UNAUTHORIZED_CHAT_ACCESS);
                }

                String finalMessage = messageDto.getMessage();

                if (ChatMessageDto.MessageType.ENTER.equals(messageDto.getType())) {
                        finalMessage = sender.getNickname() + "님이 입장하셨습니다.";
                }

                ChatMessage chatMessage = ChatMessage.builder()
                                .chatRoom(chatRoom)
                                .sender(sender)
                                .message(finalMessage)
                                .type(messageDto.getType())
                                .build();
                chatMessageRepository.save(chatMessage);

                return ChatMessageDto.builder()
                                .type(messageDto.getType())
                                .roomId(messageDto.getRoomId())
                                .senderId(messageDto.getSenderId())
                                .message(finalMessage)
                                .build();
        }

        public List<ChatRoomResponseDto> getMyChatRooms(Long userId) {
                List<ChatRoom> chatRooms = chatRoomRepository.findByHostIdOrGuestId(userId, userId);
                return chatRooms.stream()
                                .map(room -> new ChatRoomResponseDto(room, userId))
                                .collect(Collectors.toList());
        }
}