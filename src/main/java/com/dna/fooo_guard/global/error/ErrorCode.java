package com.dna.fooo_guard.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.attribute.UserDefinedFileAttributeView;

import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common Errors
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "올바르지 않은 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러가 발생했습니다."),

    // JWT Errors
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 구조의 토큰입니다."),
    SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, "토큰 서명이 일치하지 않습니다."),

    // JWT refresh token Errors
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰 형식입니다."),

    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // UserGroup Errors
    USER_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저의 그룹을 찾을 수 없습니다."),

    // Food Errors
    FOOD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 음식을 찾을 수 없습니다."),

    // Group Errors
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 그룹을 찾을 수 없습니다."),

    // Post Errors
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다");

    private final HttpStatus status;
    private final String message;
}