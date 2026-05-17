package com.dna.fooo_guard.domain.donation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.comment.entity.Comment;
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
    private final CommentRepository commentRepository;

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

        if (food.getStatus() != FoodStatus.AVAILABLE) {
            throw new CustomException(ErrorCode.FOOD_NOT_AVAILABLE);
        }

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
        // TODO: N+1
        return donationRepository.findAll().stream()
                .map(DonationResponse::from)
                .toList();
    }

    public DonationResponse findDonationById(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        return DonationResponse.from(donation);
    }

    @Transactional
    public void editDonation(Long donationId, Long userId, DonationEditRequest dto) {
        Donation donation = getDonationWithAccessCheck(donationId, userId);

        donation.getPost().edit(
                PostEditRequest.builder()
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .build());

        if (dto.getFoodId() != null && !donation.getFood().getId().equals(dto.getFoodId())) {
            Food newFood = foodRepository.findById(dto.getFoodId())
                    .orElseThrow(() -> new CustomException(ErrorCode.FOOD_NOT_FOUND));

            if (newFood.getStatus() != FoodStatus.AVAILABLE) {
                throw new CustomException(ErrorCode.FOOD_NOT_AVAILABLE);
            }

            donation.getFood().updateStatus(FoodStatus.AVAILABLE);
            newFood.updateStatus(FoodStatus.DONATED);
            donation.edit(newFood);
        }
    }

    @Transactional
    public void deleteDonation(Long donationId, Long userId) {
        Donation donation = getDonationWithAccessCheck(donationId, userId);
        Post post = donation.getPost();

        // 연관된 음식 상태 복원
        donation.getFood().updateStatus(FoodStatus.AVAILABLE);

        List<Comment> comments = commentRepository.findAllByPostId(post.getId());
        for (Comment comment : comments) {
            comment.delete();
        }

        donationRepository.delete(donation);
        postRepository.delete(post);
    }
}
