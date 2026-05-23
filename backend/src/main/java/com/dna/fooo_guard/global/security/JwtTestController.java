package com.dna.fooo_guard.global.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test/jwt")
@RequiredArgsConstructor
public class JwtTestController {

    private final JwtTokenProvider jwtTokenProvider;

    // 1. 토큰 생성 테스트: /api/test/jwt/create?id=1
    @GetMapping("/create")
    public String create(@RequestParam("id") Long id) {
        return jwtTokenProvider.createToken(id);
    }

    // 2. 토큰 해석 테스트: /api/test/jwt/extract?token=xxxxx
    @GetMapping("/extract")
    public Long extract(@RequestParam("token") String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
