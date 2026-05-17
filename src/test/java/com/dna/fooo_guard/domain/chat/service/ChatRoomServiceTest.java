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

import com.dna.fooo_guard.domain.chat.dto.ChatRoomResponse;
import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.entity.ChatRoomStatus;
import com.dna.fooo_guard.domain.chat.repository.ChatRoomRepository;
import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.donation.repository.DonationRepository;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private DonationRepository donationRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private User owner;
    private User requester;
    private Donation donation;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("owner").nickname("작성자").build();
        requester = User.builder().id(2L).username("requester").nickname("문의자").build();
        Post post = Post.builder().id(10L).title("나눔글").user(owner).build();
        Food food = Food.builder().id(20L).name("음식").user(owner).build();
        donation = Donation.builder().id(30L).post(post).food(food).build();
        chatRoom = ChatRoom.builder()
                .id(40L)
                .donation(donation)
                .owner(owner)
                .requester(requester)
                .status(ChatRoomStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("성공 - 이미 존재하는 채팅방 반환")
    void startChatRoom_ReturnsExistingRoom() {
        when(donationRepository.findById(30L)).thenReturn(Optional.of(donation));
        when(chatRoomRepository.findByDonationIdAndRequesterId(30L, 2L)).thenReturn(Optional.of(chatRoom));

        ChatRoomResponse response = chatRoomService.startChatRoom(30L, 2L);

        assertEquals(40L, response.getId());
        assertEquals(1L, response.getOpponentId());
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("실패 - 자기 나눔글에 채팅 시작")
    void startChatRoom_CannotChatWithSelf() {
        when(donationRepository.findById(30L)).thenReturn(Optional.of(donation));

        CustomException exception = assertThrows(CustomException.class, () -> {
            chatRoomService.startChatRoom(30L, 1L);
        });

        assertEquals(ErrorCode.CANNOT_CHAT_WITH_SELF, exception.getErrorCode());
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("성공 - 채팅방 닫기")
    void closeChatRoom_Success() {
        when(chatRoomRepository.findById(40L)).thenReturn(Optional.of(chatRoom));

        chatRoomService.closeChatRoom(40L, 1L);

        assertEquals(ChatRoomStatus.CLOSED, chatRoom.getStatus());
    }

    @Test
    @DisplayName("실패 - 참여자가 아닌 사용자의 채팅방 닫기")
    void closeChatRoom_AccessDenied() {
        when(chatRoomRepository.findById(40L)).thenReturn(Optional.of(chatRoom));

        CustomException exception = assertThrows(CustomException.class, () -> {
            chatRoomService.closeChatRoom(40L, 999L);
        });

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }
}
