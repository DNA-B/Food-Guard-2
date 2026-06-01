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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Donation", description = "나눔 게시글 API")
@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @Operation(summary = "나눔 등록", description = "로그인한 사용자가 보유 식품으로 나눔 게시글을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @PostMapping
    public ResponseEntity<Void> createDonation(
            @Valid @RequestBody DonationCreateRequest dto,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        donationService.createDonation(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "나눔 목록 조회", description = "전체 나눔 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DonationResponse.class))))
    @GetMapping
    public ResponseEntity<List<DonationResponse>> getDonations() {
        return ResponseEntity.ok(donationService.findAllDonations());
    }

    @Operation(summary = "나눔 단건 조회", description = "나눔 ID로 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = DonationResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<DonationResponse> getDonation(
            @Parameter(description = "나눔 ID", example = "1") @PathVariable("id") Long id) {
        return ResponseEntity.ok(donationService.findDonationById(id));
    }

    @Operation(summary = "나눔 수정", description = "나눔 작성자가 나눔 게시글 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/{id}")
    public ResponseEntity<Void> editDonation(
            @Parameter(description = "나눔 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DonationEditRequest dto) {
        donationService.editDonation(id, userId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "나눔 삭제", description = "나눔 작성자가 나눔 게시글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(
            @Parameter(description = "나눔 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        donationService.deleteDonation(id, userId);
        return ResponseEntity.ok().build();
    }
}
