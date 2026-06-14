package com.dna.fooo_guard.domain.donation.repository;

import java.util.List;
import java.util.Optional;

import com.dna.fooo_guard.domain.donation.entity.Donation;

public interface DonationRepositoryCustom {
    Optional<Donation> findByIdWithPostAndFoodAndUser(Long donationId);

    List<Donation> findAllWithPostAndFoodAndUser();
}
