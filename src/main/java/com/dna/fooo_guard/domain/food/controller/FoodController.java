package com.dna.fooo_guard.domain.food.controller;

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

import com.dna.fooo_guard.domain.food.dto.FoodCreateRequest;
import com.dna.fooo_guard.domain.food.dto.FoodEditRequest;
import com.dna.fooo_guard.domain.food.dto.FoodResponse;
import com.dna.fooo_guard.domain.food.service.FoodService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Food", description = "사용자 식품 관리 API")
@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @Operation(summary = "식품 등록", description = "로그인한 사용자의 식품을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공 (Created)"),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE : 올바르지 않은 입력값", content = @Content),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND : 유저를 찾을 수 없음 / GROUP_NOT_FOUND : 입력된 냉장고 그룹을 찾을 수 없음 (그룹 식품 등록 시)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> createFood(@Valid @RequestBody FoodCreateRequest dto,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        foodService.createFood(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "내 식품 목록 조회", description = "로그인한 사용자가 등록한 식품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FoodResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<FoodResponse>> getFoods(@Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(foodService.findAllFoodByUserId(userId));
    }

    @Operation(summary = "식품 단건 조회", description = "식품 ID로 로그인한 사용자의 식품 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = FoodResponse.class))),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED : 접근 권한이 없습니다 (본인 혹은 소속 그룹의 식품이 아님)", content = @Content),
            @ApiResponse(responseCode = "404", description = "FOOD_NOT_FOUND : 해당 식품을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFood(
            @Parameter(description = "식품 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(foodService.findFoodByIdAndUserId(id, userId));
    }

    @Operation(summary = "식품 수정", description = "식품 ID로 로그인한 사용자의 식품 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공 (OK)"),
            @ApiResponse(responseCode = "400", description = "INVALID_INPUT_VALUE : 올바르지 않은 입력값", content = @Content),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED : 접근 권한이 없습니다 (식품 수정 권한 없음)", content = @Content),
            @ApiResponse(responseCode = "404", description = "FOOD_NOT_FOUND : 해당 식품을 찾을 수 없음", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> editFood(@Parameter(description = "식품 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody FoodEditRequest dto) {
        foodService.editFood(id, userId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "식품 삭제", description = "식품 ID로 로그인한 사용자의 식품을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공 (OK)"),
            @ApiResponse(responseCode = "400", description = "CANNOT_DELETE_DONATED_FOOD : 나눔 중인 식품은 삭제할 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "403", description = "ACCESS_DENIED : 접근 권한이 없습니다 (식품 삭제 권한 없음)", content = @Content),
            @ApiResponse(responseCode = "404", description = "FOOD_NOT_FOUND : 해당 식품을 찾을 수 없음", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@Parameter(description = "식품 ID", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId) {
        foodService.deleteFood(id, userId);
        return ResponseEntity.ok().build();
    }
}