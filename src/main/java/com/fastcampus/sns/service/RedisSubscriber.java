package com.fastcampus.sns.service;

import com.fastcampus.sns.model.Alarm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final SseEmitter sseEmitter;

    private final static ObjectMapper mapper = new ObjectMapper();
    private final static String ALARM_NAME = "alarm";

    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] pattern) {

        Alarm alarm = mapper.readValue(message.getBody(), Alarm.class);
        Integer userId = alarm.getUser().getId();

        try {
            sseEmitter.send(
                    SseEmitter.event().id(alarm.toString()).name(ALARM_NAME).data("new alarm")
            );
        } catch (IOException e) {
            redisMessageListenerContainer.removeMessageListener(this, new ChannelTopic(userId.toString()));
        }
    }
}