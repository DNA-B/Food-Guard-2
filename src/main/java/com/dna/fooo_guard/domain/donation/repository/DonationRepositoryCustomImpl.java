package com.dna.fooo_guard.domain.donation.repository;

import java.util.List;
import java.util.Optional;

import com.dna.fooo_guard.domain.donation.entity.Donation;
import com.dna.fooo_guard.domain.donation.entity.QDonation;
import com.dna.fooo_guard.domain.food.entity.QFood;
import com.dna.fooo_guard.domain.post.entity.QPost;
import com.dna.fooo_guard.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DonationRepositoryCustomImpl implements DonationRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QDonation donation = QDonation.donation;
    private final QPost post = QPost.post;
    private final QFood food = QFood.food;
    private final QUser user = QUser.user;

    @Override
    public Optional<Donation> findByIdWithPostAndFoodAndUser(Long donationId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(donation)
                .join(donation.post, post).fetchJoin()
                .join(donation.food, food).fetchJoin()
                .join(donation.post.user, user).fetchJoin()
                .where(donation.id.eq(donationId))
                .fetchOne());
    }

    @Override
    public List<Donation> findAllWithPostAndFoodAndUser() {
        return queryFactory
                .selectFrom(donation)
                .join(donation.post, post).fetchJoin()
                .join(donation.food, food).fetchJoin()
                .join(donation.post.user, user).fetchJoin()
                .fetch();
    }

}