package com.dna.fooo_guard.domain.donation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.donation.dto.DonationCreateRequest;
import com.dna.fooo_guard.domain.donation.dto.DonationEditRequest;
import com.dna.fooo_guard.domain.donation.dto.DonationResponse;
import com.dna.fooo_guard.domain.donation.service.DonationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public ResponseEntity<Void> createDonation(
            @Valid @RequestBody DonationCreateRequest dto,
            @AuthenticationPrincipal Long userId) {
        donationService.createDonation(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<DonationResponse>> getDonations() {
        return ResponseEntity.ok(donationService.findAllDonations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationResponse> getDonation(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(donationService.findDonationById(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editDonation(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DonationEditRequest dto) {
        donationService.editDonation(id, userId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Long userId) {
        donationService.deleteDonation(id, userId);
        return ResponseEntity.ok().build();
    }
}