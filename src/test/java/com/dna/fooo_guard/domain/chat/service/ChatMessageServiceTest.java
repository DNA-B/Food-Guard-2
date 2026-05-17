package com.dna.fooo_guard.domain.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageCreateRequest;
import com.dna.fooo_guard.domain.chat.dto.ChatMessageResponse;
import com.dna.fooo_guard.domain.chat.entity.ChatMessage;
import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.entity.ChatRoomStatus;
import com.dna.fooo_guard.domain.chat.repository.ChatMessageRepository;
import com.dna.fooo_guard.domain.chat.repository.ChatRoomRepository;
import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatMessageService chatMessageService;

    private User owner;
    private User requester;
    private ChatRoom chatRoom;
    private ChatMessageCreateRequest request;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("owner").nickname("작성자").build();
        requester = User.builder().id(2L).username("requester").nickname("문의자").build();
        Post post = Post.builder().id(10L).title("나눔글").user(owner).build();
        Food food = Food.builder().id(20L).name("음식").user(owner).build();
        Donation donation = Donation.builder().id(30L).post(post).food(food).build();
        chatRoom = ChatRoom.builder()
                .id(40L)
                .donation(donation)
                .owner(owner)
                .requester(requester)
                .status(ChatRoomStatus.OPEN)
                .build();
        request = ChatMessageCreateRequest.builder()
                .content("안녕하세요")
                .build();
    }

    @Test
    @DisplayName("성공 - 메시지 저장 및 채팅방 마지막 메시지 갱신")
    void createMessage_Success() {
        ChatMessage savedMessage = ChatMessage.builder()
                .id(50L)
                .chatRoom(chatRoom)
                .sender(requester)
                .content("안녕하세요")
                .build();

        when(chatRoomRepository.findById(40L)).thenReturn(Optional.of(chatRoom));
        when(userRepository.getReferenceById(2L)).thenReturn(requester);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(savedMessage);

        ChatMessageResponse response = chatMessageService.createMessage(40L, 2L, request);

        assertEquals(50L, response.getId());
        assertEquals("안녕하세요", response.getContent());
        assertEquals("안녕하세요", chatRoom.getLastMessage());
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("실패 - 참여자가 아닌 사용자의 메시지 전송")
    void createMessage_AccessDenied() {
        when(chatRoomRepository.findById(40L)).thenReturn(Optional.of(chatRoom));

        CustomException exception = assertThrows(CustomException.class, () -> {
            chatMessageService.createMessage(40L, 999L, request);
        });

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("실패 - 닫힌 채팅방 메시지 전송")
    void createMessage_ClosedRoom() {
        chatRoom.close();
        when(chatRoomRepository.findById(40L)).thenReturn(Optional.of(chatRoom));

        CustomException exception = assertThrows(CustomException.class, () -> {
            chatMessageService.createMessage(40L, 2L, request);
        });

        assertEquals(ErrorCode.CHAT_ROOM_CLOSED, exception.getErrorCode());
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }
}
