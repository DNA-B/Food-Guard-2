package com.dna.fooo_guard.global.security;

import java.util.Collections;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 웹소켓 최초 연결 요청일 때, 토큰 검증
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            log.info("[WebSocket] CONNECT 요청 헤더 수신 - Authorization: {}", bearerToken);

            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);

                try {
                    jwtTokenProvider.validateToken(token);
                    Long userId = jwtTokenProvider.getUserIdFromToken(token);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.emptyList());

                    // STOMP 세션에 유저 인증 정보 주입
                    accessor.setUser(authentication);
                    log.info("[WebSocket] 인증 성공 - UserId: {}", userId);

                } catch (Exception e) {
                    log.error("[WebSocket] 토큰 검증 실패: 유효하지 않은 토큰입니다.", e);
                    throw e;
                }
            } else {
                log.error("[WebSocket] 연결 거부: Authorization 헤더가 누락되었거나 Bearer 타입이 아닙니다.");
                throw new IllegalArgumentException("인증 헤더가 유효하지 않습니다.");
            }
        }
        return message;
    }
}