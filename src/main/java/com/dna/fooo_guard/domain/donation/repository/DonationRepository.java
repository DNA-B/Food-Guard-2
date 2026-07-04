package com.dna.fooo_guard.domain.donation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dna.fooo_guard.domain.donation.entity.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long>, DonationRepositoryCustom {

}
