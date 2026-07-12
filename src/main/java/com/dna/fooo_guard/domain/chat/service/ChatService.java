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

                String finalMessage = messageDto.getMessage();

                // 입장 메시지일 경우
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