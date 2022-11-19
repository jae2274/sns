package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {
    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;

    private final RedisMessageListenerContainer redisMessageListenerContainer;


    private final RedisSubscriber redisSubscriber;
    private final RedisPublisher redisPublisher;

    private void disconnectIfExisted(Integer userId) {
        emitterRepository.get(userId).ifPresent(sseEmitter -> {
            sseEmitter.complete();
            emitterRepository.delete(userId);
        });
    }

    public SseEmitter connectAlarm(Integer userId) {
        disconnectIfExisted(userId);

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);

        sseEmitter.onTimeout(() -> {
            redisMessageListenerContainer.removeMessageListener(redisSubscriber, new ChannelTopic(userId.toString()));
            emitterRepository.delete(userId);
        });

        try {
            sseEmitter.send(
                    SseEmitter.event().id("").name(ALARM_NAME).data("connect completed")
            );
        } catch (IOException e) {
            emitterRepository.delete(userId);
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        redisMessageListenerContainer.removeMessageListener(redisSubscriber, new ChannelTopic(userId.toString()));
        redisMessageListenerContainer.addMessageListener(redisSubscriber, new ChannelTopic(userId.toString()));

        return sseEmitter;
    }

    public void send(Alarm alarm, Integer userId) {
        redisPublisher.publish(new ChannelTopic(userId.toString()).getTopic(), alarm);
    }
}
