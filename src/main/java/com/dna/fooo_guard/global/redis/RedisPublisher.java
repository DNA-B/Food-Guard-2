package com.dna.fooo_guard.global.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import com.dna.fooo_guard.domain.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper; // 오브젝트 -> JSON 변환용

    public void publish(ChatMessageDto message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(channelTopic.getTopic(), jsonMessage);
            log.info("[Redis Pub] 토픽 발행 성공 - RoomId: {}", message.getRoomId());
        } catch (Exception e) {
            log.error("[Redis Pub] 메시지 JSON 직렬화 실패: ", e);
        }
    }
}