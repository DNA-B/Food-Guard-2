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

import com.dna.fooo_guard.domain.chat.entity.ChatRoom;
import com.dna.fooo_guard.domain.chat.repository.ChatRoomRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private static final String CHAT_ROOM_SUBSCRIBE_PREFIX = "/sub/chatrooms/";

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearerToken = accessor.getFirstNativeHeader("Authorization");

            if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
                throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
            }

            String token = bearerToken.substring(7);
            jwtTokenProvider.validateToken(token);
            Long userId = jwtTokenProvider.getUserIdFromToken(token);

            // WebSocket 세션에도 REST 인증과 동일하게 userId를 principal로 저장한다.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, Collections.emptyList());
            accessor.setUser(authentication);
        }

        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            validateChatRoomSubscription(accessor);
        }

        return message;
    }

    private void validateChatRoomSubscription(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();

        if (!StringUtils.hasText(destination) || !destination.startsWith(CHAT_ROOM_SUBSCRIBE_PREFIX)) {
            return;
        }

        Long chatRoomId = parseChatRoomId(destination);
        Long userId = getUserId(accessor);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.getOwner().getId().equals(userId) && !chatRoom.getRequester().getId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }

    private Long parseChatRoomId(String destination) {
        String id = destination.substring(CHAT_ROOM_SUBSCRIBE_PREFIX.length());

        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private Long getUserId(StompHeaderAccessor accessor) {
        if (!(accessor.getUser() instanceof UsernamePasswordAuthenticationToken authentication)
                || !(authentication.getPrincipal() instanceof Long userId)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        return userId;
    }
}
