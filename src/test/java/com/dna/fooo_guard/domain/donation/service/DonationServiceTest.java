package com.dna.fooo_guard.domain.donation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.dna.fooo_guard.domain.post.entity.PostType;
import com.dna.fooo_guard.domain.post.repository.PostRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DonationService donationService;

    private User testUser;
    private Food testFood;
    private Post testPost;
    private Donation testDonation;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("tester").nickname("테스터").build();
        testFood = Food.builder().id(1L).name("김밥").type("분식").user(testUser).build();
        testPost = Post.builder()
                .id(1L)
                .title("나눔 제목")
                .content("나눔 내용")
                .postType(PostType.DONATION)
                .user(testUser)
                .build();
        testDonation = Donation.builder()
                .id(1L)
                .post(testPost)
                .food(testFood)
                .status(DonationStatus.ONGOING)
                .build();
    }

    @Test
    @DisplayName("나눔 생성 성공")
    void createDonation_Success() {
        DonationCreateRequest dto = donationCreateRequest("나눔 제목", "나눔 내용", 1L);
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        ArgumentCaptor<Donation> donationCaptor = ArgumentCaptor.forClass(Donation.class);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        when(userRepository.getReferenceById(1L)).thenReturn(testUser);

        donationService.createDonation(dto, 1L);

        assertEquals(FoodStatus.DONATED, testFood.getStatus());
        verify(postRepository).save(postCaptor.capture());
        verify(donationRepository).save(donationCaptor.capture());
        assertEquals(PostType.DONATION, postCaptor.getValue().getPostType());
        assertEquals(DonationStatus.ONGOING, donationCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("나눔 생성 실패 - 음식 없음")
    void createDonation_FoodNotFound() {
        DonationCreateRequest dto = donationCreateRequest("나눔 제목", "나눔 내용", 1L);

        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> donationService.createDonation(dto, 1L));

        assertEquals(ErrorCode.FOOD_NOT_FOUND, exception.getErrorCode());
        verify(postRepository, never()).save(any(Post.class));
        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    @DisplayName("전체 나눔 조회 성공")
    void findAllDonations_Success() {
        when(donationRepository.findAll()).thenReturn(List.of(testDonation));

        List<DonationResponse> responses = donationService.findAllDonations();

        assertEquals(1, responses.size());
        assertEquals("나눔 제목", responses.get(0).getTitle());
        assertEquals("김밥", responses.get(0).getFoodName());
    }

    @Test
    @DisplayName("나눔 단건 조회 성공")
    void findDonationById_Success() {
        when(donationRepository.findById(1L)).thenReturn(Optional.of(testDonation));

        DonationResponse response = donationService.findDonationById(1L, 1L);

        assertEquals(1L, response.getDonationId());
        assertEquals(1L, response.getFoodId());
    }

    @Test
    @DisplayName("나눔 단건 조회 실패 - 권한 없음")
    void findDonationById_AccessDenied() {
        when(donationRepository.findById(1L)).thenReturn(Optional.of(testDonation));

        CustomException exception = assertThrows(CustomException.class,
                () -> donationService.findDonationById(1L, 999L));

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    @Test
    @DisplayName("나눔 수정 성공")
    void editDonation_Success() {
        Food newFood = Food.builder().id(2L).name("샌드위치").type("간식").user(testUser).build();
        DonationEditRequest dto = donationEditRequest("수정 제목", "수정 내용", 2L);

        when(donationRepository.findById(1L)).thenReturn(Optional.of(testDonation));
        when(foodRepository.getReferenceById(2L)).thenReturn(newFood);

        donationService.editDonation(1L, 1L, dto);

        assertEquals("수정 제목", testDonation.getPost().getTitle());
        assertEquals("수정 내용", testDonation.getPost().getContent());
        assertEquals(2L, testDonation.getFood().getId());
    }

    @Test
    @DisplayName("나눔 수정 실패 - 나눔 없음")
    void editDonation_DonationNotFound() {
        DonationEditRequest dto = donationEditRequest("수정 제목", "수정 내용", 2L);

        when(donationRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> donationService.editDonation(1L, 1L, dto));

        assertEquals(ErrorCode.DONATION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("나눔 삭제 성공")
    void deleteDonation_Success() {
        when(donationRepository.findById(1L)).thenReturn(Optional.of(testDonation));

        donationService.deleteDonation(1L, 1L);

        verify(donationRepository).delete(testDonation);
        verify(postRepository).delete(testPost);
    }

    @Test
    @DisplayName("나눔 삭제 실패 - 권한 없음")
    void deleteDonation_AccessDenied() {
        when(donationRepository.findById(1L)).thenReturn(Optional.of(testDonation));

        CustomException exception = assertThrows(CustomException.class, () -> donationService.deleteDonation(1L, 999L));

        assertEquals(ErrorCode.ACCESS_DENIED, exception.getErrorCode());
        verify(donationRepository, never()).delete(any(Donation.class));
        verify(postRepository, never()).delete(any(Post.class));
    }

    private DonationCreateRequest donationCreateRequest(String title, String content, Long foodId) {
        return DonationCreateRequest.builder()
                .title(title)
                .content(content)
                .foodId(foodId)
                .build();
    }

    private DonationEditRequest donationEditRequest(String title, String content, Long foodId) {
        return DonationEditRequest.builder()
                .title(title)
                .content(content)
                .foodId(foodId)
                .build();
    }
}
