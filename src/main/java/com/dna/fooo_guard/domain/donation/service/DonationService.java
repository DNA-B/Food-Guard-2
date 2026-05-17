package com.dna.fooo_guard.domain.donation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.donation.dto.DonationCreateRequest;
import com.dna.fooo_guard.domain.donation.dto.DonationEditRequest;
import com.dna.fooo_guard.domain.donation.dto.DonationResponse;
import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.donation.entity.DonationStatus;
import com.dna.fooo_guard.domain.donation.repository.DonationRepository;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.food.entity.FoodStatus;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.post.dto.PostEditRequest;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.post.entity.PostType;
import com.dna.fooo_guard.domain.post.repository.PostRepository;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DonationService {
    private final DonationRepository donationRepository;
    private final PostRepository postRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    private Donation getDonationWithAccessCheck(Long donationId, Long userId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new CustomException(ErrorCode.DONATION_NOT_FOUND));

        if (!donation.getPost().getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return donation;
    }

    @Transactional
    public void createDonation(DonationCreateRequest dto, Long userId) {
        Food food = foodRepository.findById(dto.getFoodId())
                .orElseThrow(() -> new CustomException(ErrorCode.FOOD_NOT_FOUND));
        food.updateStatus(FoodStatus.DONATED);

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .postType(PostType.DONATION)
                .user(userRepository.getReferenceById(userId))
                .build();
        postRepository.save(post);

        Donation donation = Donation.builder()
                .post(post)
                .food(food)
                .status(DonationStatus.ONGOING)
                .build();
        donationRepository.save(donation);
    }

    public List<DonationResponse> findAllDonations() {
        return donationRepository.findAll().stream()
                .map(DonationResponse::from)
                .toList();
    }

    public DonationResponse findDonationById(Long donationId, Long userId) {
        Donation donation = getDonationWithAccessCheck(donationId, userId);
        return DonationResponse.from(donation);
    }

    // TODO: 수정 검토
    @Transactional
    public void editDonation(Long donationId, Long userId, DonationEditRequest dto) {
        Donation donation = getDonationWithAccessCheck(donationId, userId);
        donation.getPost()
                .edit(PostEditRequest.builder()
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .build());

        Food newFood = foodRepository.getReferenceById(dto.getFoodId());
        donation.edit(newFood);
    }

    @Transactional
    public void deleteDonation(Long donationId, Long userId) {
        Donation donation = getDonationWithAccessCheck(donationId, userId);
        donationRepository.delete(donation);
        postRepository.delete(donation.getPost());
    }
}
