package com.dna.fooo_guard.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dna.fooo_guard.domain.chat.dto.ChatRoomResponseDto;
import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.repository.ChatMessageRepository;
import com.dna.fooo_guard.domain.chat.repository.ChatRoomRepository;
import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.donation.entity.DonationStatus;
import com.dna.fooo_guard.domain.donation.repository.DonationRepository;
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

    private static Donation createDonation(Long id, User host, DonationStatus status) {
        Post post = Post.builder()
                .id(10L)
                .user(host)
                .build();

        return Donation.builder()
                .id(id)
                .post(post)
                .status(status)
                .build();
    }

    private static ChatRoom createRoom(Long id, Donation donation, User host, User guest) {
        return ChatRoom.builder()
                .id(id)
                .donation(donation)
                .host(host)
                .guest(guest)
                .build();
    }

    @Nested
    @DisplayName("채팅방 생성")
    class CreateChatRoom {

        @Test
        @DisplayName("새 채팅방 생성 성공")
        void createChatRoom_Success_NewRoom() {
            Long donationId = 100L;
            Long hostId = 1L;
            Long guestId = 2L;
            Long roomId = 300L;

            User host = createUser(hostId, "host");
            User guest = createUser(guestId, "guest");
            Donation donation = createDonation(donationId, host, DonationStatus.ONGOING);

            given(donationRepository.findById(donationId)).willReturn(Optional.of(donation));
            given(userRepository.findById(guestId)).willReturn(Optional.of(guest));
            given(chatRoomRepository.findByDonationIdAndGuestId(donationId, guestId)).willReturn(Optional.empty());
            given(chatRoomRepository.save(any(ChatRoom.class))).willReturn(ChatRoom.builder().id(roomId).build());

            Long result = chatService.createChatRoom(donationId, guestId);

            assertThat(result).isEqualTo(roomId);

            ArgumentCaptor<ChatRoom> captor = ArgumentCaptor.forClass(ChatRoom.class);
            verify(chatRoomRepository).save(captor.capture());
            assertThat(captor.getValue().getDonation()).isEqualTo(donation);
            assertThat(captor.getValue().getHost()).isEqualTo(host);
            assertThat(captor.getValue().getGuest()).isEqualTo(guest);
        }

        @Test
        @DisplayName("이미 존재하는 채팅방이면 기존 방 ID 반환")
        void createChatRoom_Success_ExistingRoom() {
            Long donationId = 100L;
            Long guestId = 2L;
            Long roomId = 300L;

            User host = createUser(1L, "host");
            User guest = createUser(guestId, "guest");
            Donation donation = createDonation(donationId, host, DonationStatus.ONGOING);
            ChatRoom existingRoom = createRoom(roomId, donation, host, guest);

            given(donationRepository.findById(donationId)).willReturn(Optional.of(donation));
            given(userRepository.findById(guestId)).willReturn(Optional.of(guest));
            given(chatRoomRepository.findByDonationIdAndGuestId(donationId, guestId))
                    .willReturn(Optional.of(existingRoom));

            Long result = chatService.createChatRoom(donationId, guestId);

            assertThat(result).isEqualTo(roomId);
            verify(chatRoomRepository, never()).save(any(ChatRoom.class));
        }

        @Test
        @DisplayName("나눔 작성자는 자기 글에 채팅방을 만들 수 없음")
        void createChatRoom_Fail_CannotChatWithSelf() {
            Long donationId = 100L;
            Long hostId = 1L;

            User host = createUser(hostId, "host");
            Donation donation = createDonation(donationId, host, DonationStatus.ONGOING);

            given(donationRepository.findById(donationId)).willReturn(Optional.of(donation));
            given(userRepository.findById(hostId)).willReturn(Optional.of(host));

            Throwable thrown = catchThrowable(() -> chatService.createChatRoom(donationId, hostId));

            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.CANNOT_CHAT_WITH_SELF));
            verify(chatRoomRepository, never()).save(any(ChatRoom.class));
        }
    }

    @Nested
    @DisplayName("내 채팅방 조회")
    class GetMyChatRooms {

        @Test
        @DisplayName("완료된 나눔 채팅방은 목록에서 제외")
        void getMyChatRooms_FiltersCompletedDonationRooms() {
            Long userId = 1L;
            User host = createUser(userId, "host");
            User guest = createUser(2L, "guest");
            Donation ongoingDonation = createDonation(100L, host, DonationStatus.ONGOING);
            Donation completedDonation = createDonation(200L, host, DonationStatus.COMPLETED);

            ChatRoom ongoingRoom = createRoom(10L, ongoingDonation, host, guest);
            ChatRoom completedRoom = createRoom(20L, completedDonation, host, guest);

            given(chatRoomRepository.findByHostIdOrGuestId(userId, userId))
                    .willReturn(List.of(ongoingRoom, completedRoom));

            List<ChatRoomResponseDto> result = chatService.getMyChatRooms(userId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRoomId()).isEqualTo(10L);
            assertThat(result.get(0).getDonationId()).isEqualTo(100L);
            assertThat(result.get(0).getOpponentNickname()).isEqualTo("guest");
        }
    }
}
