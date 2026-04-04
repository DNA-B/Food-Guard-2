package com.dna.fooo_guard.global.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            try {
                // User ID 가져오기
                Long userId = jwtTokenProvider.getUserIdFromToken(token);

                // 인증 객체 생성 (권한은 일단 "USER"로 고정)
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

                // 시큐리티 세션(Context)에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (Exception e) {
                // 일단 그냥 통과시켜서 다음 필터에서 걸러지게 두기.
            }
        }

        filterChain.doFilter(request, response);
    }
}
