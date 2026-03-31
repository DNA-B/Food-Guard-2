package com.dna.fooo_guard.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getStatus().value()) // 400, 404 등 숫자
                        .error(errorCode.getStatus().name()) // BAD_REQUEST, NOT_FOUND 등 문자열
                        .message(errorCode.getMessage())
                        .build());
    }
}