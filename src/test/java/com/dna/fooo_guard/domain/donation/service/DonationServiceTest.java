package com.dna.fooo_guard.domain.donation.service;

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

import com.dna.fooo_guard.domain.comment.repository.CommentRepository;
import com.dna.fooo_guard.domain.donation.dto.DonationCreateRequest;
import com.dna.fooo_guard.domain.donation.dto.DonationEditRequest;
import com.dna.fooo_guard.domain.donation.dto.DonationResponse;
import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.donation.entity.DonationStatus;
import com.dna.fooo_guard.domain.donation.repository.DonationRepository;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.food.entity.FoodStatus;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.post.repository.PostRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private DonationService donationService;

    // ------------------ [HELPERS] ------------------

    private static User createUser(Long userId) {
        return User.builder()
                .id(userId)
                .username("testUser")
                .build();
    }

    private static Post createPost(Long postId, User user) {
        return Post.builder()
                .id(postId)
                .title("나눔글 제목")
                .content("나눔글 내용")
                .user(user)
                .build();
    }

    private static Food createFood(Long foodId, FoodStatus status) {
        return Food.builder()
                .id(foodId)
                .name("포테이토 피자")
                .status(status)
                .build();
    }

    private static Donation createDonation(Long donationId, Post post, Food food, DonationStatus status) {
        return Donation.builder()
                .id(donationId)
                .post(post)
                .food(food)
                .status(status)
                .build();
    }

    @Nested
    @DisplayName("성공 케이스")
    class Success {

        @Test
        @DisplayName("나눔 글 생성 성공 - 음식이 DONATED 상태로 변하고 Post와 Donation이 저장된다")
        void createDonation_Success() {
            // given
            Long userId = 1L;
            Long foodId = 50L;
            User user = createUser(userId);
            Food food = createFood(foodId, FoodStatus.AVAILABLE);
            DonationCreateRequest request = DonationCreateRequest.builder()
                    .title("피자 나눔합니다")
                    .content("빨리 가져가세요")
                    .foodId(foodId)
                    .build();

            given(foodRepository.findById(foodId)).willReturn(Optional.of(food));
            given(userRepository.getReferenceById(userId)).willReturn(user);

            // when
            donationService.createDonation(request, userId);

            // then
            assertThat(food.getStatus()).isEqualTo(FoodStatus.DONATED); // 음식 상태 변경 확인
            verify(postRepository).save(any(Post.class)); // 게시글 영속화 확인

            ArgumentCaptor<Donation> donationCaptor = ArgumentCaptor.forClass(Donation.class);
            verify(donationRepository).save(donationCaptor.capture()); // 나눔글 영속화 확인

            Donation savedDonation = donationCaptor.getValue();
            assertThat(savedDonation.getStatus()).isEqualTo(DonationStatus.ONGOING);
        }

        @Test
        @DisplayName("나눔 글 전체 조회 성공")
        void findAllDonations_Success() {
            // given
            User host = createUser(1L);
            Post post1 = createPost(10L, host);
            Post post2 = createPost(11L, host);
            Food food1 = createFood(50L, FoodStatus.DONATED);
            Food food2 = createFood(51L, FoodStatus.DONATED);

            List<Donation> donations = List.of(
                    createDonation(100L, post1, food1, DonationStatus.ONGOING),
                    createDonation(200L, post2, food2, DonationStatus.COMPLETED));

            given(donationRepository.findAllWithPostAndFoodAndUser()).willReturn(donations);

            // when
            List<DonationResponse> responses = donationService.findAllDonations();

            // then
            assertThat(responses).hasSize(2);
            verify(donationRepository).findAllWithPostAndFoodAndUser();
        }

        @Test
        @DisplayName("나눔 글 상세 조회 성공")
        void findDonationById_Success() {
            // given
            Long donationId = 100L;
            User host = createUser(1L);
            Post post = createPost(10L, host);
            Food food = createFood(50L, FoodStatus.DONATED);
            Donation donation = createDonation(donationId, post, food, DonationStatus.ONGOING);

            given(donationRepository.findByIdWithPostAndFoodAndUser(donationId)).willReturn(Optional.of(donation));

            // when
            DonationResponse response = donationService.findDonationById(donationId);

            // then
            assertThat(response).isNotNull();
            verify(donationRepository).findByIdWithPostAndFoodAndUser(donationId);
        }

        @Test
        @DisplayName("나눔 글 수정 성공 - 음식 변경 없음")
        void editDonation_Success_NoFoodChange() {
            // given
            Long donationId = 100L;
            Long userId = 1L;
            User host = createUser(userId);
            Post post = createPost(10L, host); // "나눔글 제목"
            Food food = createFood(50L, FoodStatus.DONATED);
            Donation donation = createDonation(donationId, post, food, DonationStatus.ONGOING);

            DonationEditRequest request = DonationEditRequest.builder()
                    .title("수정된 제목")
                    .content("수정된 내용")
                    .foodId(50L) // 기존 음식과 동일
                    .build();

            given(donationRepository.findByIdWithPostAndFoodAndUser(donationId)).willReturn(Optional.of(donation));

            // when
            donationService.editDonation(donationId, userId, request);

            // then
            assertThat(post.getTitle()).isEqualTo("수정된 제목");
            assertThat(post.getContent()).isEqualTo("수정된 내용");
            verify(foodRepository, never()).findById(any());
        }

        @Test
        @DisplayName("나눔 글 수정 성공 - 새로운 음식으로 변경 시 기존 음식은 AVAILABLE, 새 음식은 DONATED가 된다")
        void editDonation_Success_WithFoodChange() {
            // given
            Long donationId = 100L;
            Long userId = 1L;
            Long newFoodId = 60L;

            User host = createUser(userId);
            Post post = createPost(10L, host);
            Food oldFood = createFood(50L, FoodStatus.DONATED);
            Food newFood = createFood(newFoodId, FoodStatus.AVAILABLE);
            Donation donation = createDonation(donationId, post, oldFood, DonationStatus.ONGOING);

            DonationEditRequest request = DonationEditRequest.builder()
                    .title("제목 변경")
                    .content("내용 변경")
                    .foodId(newFoodId) // 새로운 음식 요청
                    .build();

            given(donationRepository.findByIdWithPostAndFoodAndUser(donationId)).willReturn(Optional.of(donation));
            given(foodRepository.findById(newFoodId)).willReturn(Optional.of(newFood));

            // when
            donationService.editDonation(donationId, userId, request);

            // then
            assertThat(oldFood.getStatus()).isEqualTo(FoodStatus.AVAILABLE); // 구 음식 복원
            assertThat(newFood.getStatus()).isEqualTo(FoodStatus.DONATED); // 신 음식 기부 처리
            assertThat(donation.getFood()).isEqualTo(newFood);
        }

        @Test
        @DisplayName("나눔 글 삭제 성공 - 연관 음식 상태 복원 및 댓글 Soft Delete 처리")
        void deleteDonation_Success() {
            // given
            Long donationId = 100L;
            Long userId = 1L;
            User host = createUser(userId);
            Post post = createPost(10L, host);
            Food food = createFood(50L, FoodStatus.DONATED);
            Donation donation = createDonation(donationId, post, food, DonationStatus.ONGOING);

            given(donationRepository.findByIdWithPostAndFoodAndUser(donationId)).willReturn(Optional.of(donation));
            given(commentRepository.findAllByPostId(post.getId())).willReturn(List.of());

            // when
            donationService.deleteDonation(donationId, userId);

            // then
            assertThat(food.getStatus()).isEqualTo(FoodStatus.AVAILABLE); // 음식 상태 복원 확인
            verify(donationRepository).delete(donation);
            verify(postRepository).delete(post);
        }

        @Test
        @DisplayName("나눔 완료 성공 - Donation은 COMPLETED, Food는 DONATED 상태가 된다")
        void completeDonation_Success() {
            // given
            Long donationId = 100L;
            Long userId = 1L;
            User host = createUser(userId);
            Post post = createPost(10L, host);
            Food food = createFood(50L, FoodStatus.AVAILABLE);
            Donation donation = createDonation(donationId, post, food, DonationStatus.ONGOING);

            given(donationRepository.findById(donationId)).willReturn(Optional.of(donation));

            // when
            donationService.completeDonation(donationId, userId);

            // then
            assertThat(donation.getStatus()).isEqualTo(DonationStatus.COMPLETED);
            assertThat(food.getStatus()).isEqualTo(FoodStatus.DONATED);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class Failure {

        @Test
        @DisplayName("나눔 생성 실패 - 나눔 요청한 음식을 찾을 수 없음")
        void createDonation_Fail_FoodNotFound() {
            // given
            Long userId = 1L;
            Long wrongFoodId = 999L;
            DonationCreateRequest request = DonationCreateRequest.builder().foodId(wrongFoodId).build();

            given(foodRepository.findById(wrongFoodId)).willReturn(Optional.empty());

            // when & then
            Throwable thrown = catchThrowable(() -> donationService.createDonation(request, userId));

            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.FOOD_NOT_FOUND));
        }

        @Test
        @DisplayName("나눔 생성 실패 - 음익이 기부 가능(AVAILABLE) 상태가 아님")
        void createDonation_Fail_FoodNotAvailable() {
            // given
            Long userId = 1L;
            Long foodId = 50L;
            Food logicFood = createFood(foodId, FoodStatus.DONATED); // 이미 기부된 상태
            DonationCreateRequest request = DonationCreateRequest.builder().foodId(foodId).build();

            given(foodRepository.findById(foodId)).willReturn(Optional.of(logicFood));

            // when & then
            Throwable thrown = catchThrowable(() -> donationService.createDonation(request, userId));

            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.FOOD_NOT_AVAILABLE));
        }

        @Test
        @DisplayName("나눔 상세조회 실패 - 나눔글을 찾을 수 없음")
        void findDonationById_Fail_NotFound() {
            // given
            Long wrongDonationId = 999L;
            given(donationRepository.findByIdWithPostAndFoodAndUser(wrongDonationId)).willReturn(Optional.empty());

            // when & then
            Throwable thrown = catchThrowable(() -> donationService.findDonationById(wrongDonationId));

            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.DONATION_NOT_FOUND));
        }

        @Test
        @DisplayName("나눔 수정 실패 - 타인의 나눔글을 수정하려고 시도 시 권한 예외 발생")
        void editDonation_Fail_AccessDenied() {
            // given
            Long donationId = 100L;
            Long hostId = 1L;
            Long hackerId = 2L;

            User host = createUser(hostId);
            Post post = createPost(10L, host);
            Food food = createFood(50L, FoodStatus.DONATED);
            Donation donation = createDonation(donationId, post, food, DonationStatus.ONGOING);

            DonationEditRequest request = DonationEditRequest.builder().build();

            given(donationRepository.findByIdWithPostAndFoodAndUser(donationId)).willReturn(Optional.of(donation));

            // when & then
            Throwable thrown = catchThrowable(() -> donationService.editDonation(donationId, hackerId, request));

            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(
                            ex -> assertThat(((CustomException) ex).getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED));
        }

        @Test
        @DisplayName("나눔 수정 실패 - 변경하려는 새로운 음식을 찾을 수 없음")
        void editDonation_Fail_NewFoodNotFound() {
            // given
            Long donationId = 100L;
            Long userId = 1L;
            Long wrongFoodId = 999L;

            User host = createUser(userId);
            Post post = createPost(10L, host);
            Food oldFood = createFood(50L, FoodStatus.DONATED);
            Donation donation = createDonation(donationId, post, oldFood, DonationStatus.ONGOING);

            DonationEditRequest request = DonationEditRequest.builder()
                    .foodId(wrongFoodId)
                    .build();

            given(donationRepository.findByIdWithPostAndFoodAndUser(donationId)).willReturn(Optional.of(donation));
            given(foodRepository.findById(wrongFoodId)).willReturn(Optional.empty());

            // when & then
            Throwable thrown = catchThrowable(() -> donationService.editDonation(donationId, userId, request));

            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.FOOD_NOT_FOUND));
        }

        @Test
        @DisplayName("나눔 삭제 실패 - 권한 없음")
        void deleteDonation_Fail_AccessDenied() {
            // given
            Long donationId = 100L;
            Long hostId = 1L;
            Long hackerId = 2L;

            User host = createUser(hostId);
            Post post = createPost(10L, host);
            Food food = createFood(50L, FoodStatus.DONATED);
            Donation donation = createDonation(donationId, post, food, DonationStatus.ONGOING);

            given(donationRepository.findByIdWithPostAndFoodAndUser(donationId)).willReturn(Optional.of(donation));

            // when & then
            Throwable thrown = catchThrowable(() -> donationService.deleteDonation(donationId, hackerId));

            assertThat(thrown)
                    .isInstanceOf(CustomException.class)
                    .satisfies(
                            ex -> assertThat(((CustomException) ex).getErrorCode()).isEqualTo(ErrorCode.ACCESS_DENIED));
        }
    }
}
