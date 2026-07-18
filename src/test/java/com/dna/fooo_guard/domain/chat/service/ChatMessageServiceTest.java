package com.dna.fooo_guard.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageDto;
import com.dna.fooo_guard.domain.chat.dto.ChatMessageDto.MessageType;
import com.dna.fooo_guard.domain.chat.entity.ChatMessage;
import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.repository.ChatMessageRepository;
import com.dna.fooo_guard.domain.chat.repository.ChatRoomRepository;
import com.dna.fooo_guard.domain.donation.repository.DonationRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private DonationRepository donationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatService chatService;

    private static User createUser(Long id, String nickname) {
        return User.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }

    private static ChatRoom createRoom(Long id, User host, User guest) {
        return ChatRoom.builder()
                .id(id)
                .host(host)
                .guest(guest)
                .build();
    }

    @Nested
    @DisplayName("메시지 처리")
    class ProcessMessage {

        @Test
        @DisplayName("대화 메시지 저장 성공")
        void processMessage_Success_Talk() {
            Long roomId = 100L;
            Long senderId = 1L;
            User host = createUser(senderId, "host");
            User guest = createUser(2L, "guest");
            ChatRoom room = createRoom(roomId, host, guest);
            ChatMessageDto request = ChatMessageDto.builder()
                    .type(MessageType.TALK)
                    .roomId(roomId)
                    .senderId(senderId)
                    .message("안녕하세요")
                    .build();

            given(chatRoomRepository.findById(roomId)).willReturn(Optional.of(room));
            given(userRepository.findById(senderId)).willReturn(Optional.of(host));

            ChatMessageDto result = chatService.processMessage(request);

            assertThat(result.getType()).isEqualTo(MessageType.TALK);
            assertThat(result.getRoomId()).isEqualTo(roomId);
            assertThat(result.getSenderId()).isEqualTo(senderId);
            assertThat(result.getMessage()).isEqualTo("안녕하세요");

            ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
            verify(chatMessageRepository).save(captor.capture());
            assertThat(captor.getValue().getChatRoom()).isEqualTo(room);
            assertThat(captor.getValue().getSender()).isEqualTo(host);
            assertThat(captor.getValue().getMessage()).isEqualTo("안녕하세요");
            assertThat(captor.getValue().getType()).isEqualTo(MessageType.TALK);
        }

        @Test
        @DisplayName("입장 메시지는 닉네임 기반 안내 문구로 저장")
        void processMessage_Success_Enter() {
            Long roomId = 100L;
            Long senderId = 2L;
            User host = createUser(1L, "host");
            User guest = createUser(senderId, "guest");
            ChatRoom room = createRoom(roomId, host, guest);
            ChatMessageDto request = ChatMessageDto.builder()
                    .type(MessageType.ENTER)
                    .roomId(roomId)
                    .senderId(senderId)
                    .message("ignored")
                    .build();

            given(chatRoomRepository.findById(roomId)).willReturn(Optional.of(room));
            given(userRepository.findById(senderId)).willReturn(Optional.of(guest));

            ChatMessageDto result = chatService.processMessage(request);

            assertThat(result.getMessage()).isEqualTo("guest님이 입장하셨습니다.");
            verify(chatMessageRepository).save(any(ChatMessage.class));
        }

        @Test
        @DisplayName("채팅방 참여자가 아니면 메시지 저장 실패")
        void processMessage_Fail_UnauthorizedChatAccess() {
            Long roomId = 100L;
            Long senderId = 3L;
            User host = createUser(1L, "host");
            User guest = createUser(2L, "guest");
            User outsider = createUser(senderId, "outsider");
            ChatRoom room = createRoom(roomId, host, guest);
            ChatMessageDto request = ChatMessageDto.builder()
                    .type(MessageType.TALK)
                    .roomId(roomId)
                    .senderId(senderId)
                    .message("안녕하세요")
                    .build();

            given(chatRoomRepository.findById(roomId)).willReturn(Optional.of(room));
            given(userRepository.findById(senderId)).willReturn(Optional.of(outsider));

            Throwable thrown = catchThrowable(() -> chatService.processMessage(request));

            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.UNAUTHORIZED_CHAT_ACCESS));
            verify(chatMessageRepository, never()).save(any(ChatMessage.class));
        }
    }
}
