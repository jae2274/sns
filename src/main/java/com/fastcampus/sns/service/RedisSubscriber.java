package com.fastcampus.sns.service;

import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.repository.EmitterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    private final ObjectMapper mapper;
    private final EmitterRepository emitterRepository;
    private final static String ALARM_NAME = "alarm";

    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] pattern) {
        Alarm alarm = mapper.readValue(message.getBody(), Alarm.class);

        Optional<SseEmitter> sseEmitterOpt = emitterRepository.get(
                alarm.getUser().getId()
        );

        sseEmitterOpt.ifPresentOrElse(sseEmitter -> {
            sendAlarm(alarm, sseEmitter);
        }, () -> log.info("No emitter founded."));
    }

    private void sendAlarm(Alarm alarm, SseEmitter sseEmitter) {
        Integer userId = alarm.getUser().getId();

        try {
            sseEmitter.send(
                    SseEmitter.event().id(alarm.toString()).name(ALARM_NAME).data("new alarm")
            );
        } catch (IOException e) {
            emitterRepository.delete(userId);
            redisMessageListenerContainer.removeMessageListener(this, new ChannelTopic(userId.toString()));
        }
    }
}