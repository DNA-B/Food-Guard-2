package com.dna.fooo_guard.domain.chat.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageCreateRequest;
import com.dna.fooo_guard.domain.chat.dto.ChatMessageResponse;
import com.dna.fooo_guard.domain.chat.entity.ChatMessage;
import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.entity.ChatRoomStatus;
import com.dna.fooo_guard.domain.chat.repository.ChatMessageRepository;
import com.dna.fooo_guard.domain.chat.repository.ChatRoomRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public List<ChatMessageResponse> findMessages(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = getChatRoomWithAccessCheck(chatRoomId, userId);

        return chatMessageRepository.findAllByChatRoomIdOrderByCreatedAtAsc(chatRoom.getId()).stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    @Transactional
    public ChatMessageResponse createMessage(Long chatRoomId, Long senderId, ChatMessageCreateRequest dto) {
        ChatRoom chatRoom = getChatRoomWithAccessCheck(chatRoomId, senderId);

        if (chatRoom.getStatus() == ChatRoomStatus.CLOSED) {
            throw new CustomException(ErrorCode.CHAT_ROOM_CLOSED);
        }

        User sender = userRepository.getReferenceById(senderId);
        ChatMessage chatMessage = dto.toEntity(chatRoom, sender);
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        chatRoom.updateLastMessage(savedChatMessage.getContent(), LocalDateTime.now());

        return ChatMessageResponse.from(savedChatMessage);
    }

    private ChatRoom getChatRoomWithAccessCheck(Long id, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.getOwner().getId().equals(userId) && !chatRoom.getRequester().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return chatRoom;
    }
}
