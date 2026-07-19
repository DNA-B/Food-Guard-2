package com.dna.fooo_guard.global.redis;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    // Redis 컨테이너로부터 메시지가 Push되면 자동으로 트리거
    public void onMessage(String message) {
        try {
            ChatMessageDto chatMessage = objectMapper.readValue(message, ChatMessageDto.class);
            // 소켓 연결자들에게 브로드캐스팅
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
            log.info("[Redis Sub] 메시지 수신 후 소켓 전송 완료 - RoomId: {}", chatMessage.getRoomId());

        } catch (Exception e) {
            log.error("[Redis Sub] 수신 메시지 역직렬화 실패: ", e);
        }
    }
}