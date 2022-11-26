package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.model.AlarmArgs;
import com.fastcampus.sns.model.AlarmType;
import com.fastcampus.sns.model.entity.AlarmEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.AlarmEntityRepository;
import com.fastcampus.sns.repository.UserRepository;
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

    private final RedisMessageListenerContainer redisMessageListenerContainer;


    private final RedisPublisher redisPublisher;
    private final AlarmEntityRepository alarmEntityRepository;
    private final UserRepository userRepository;

    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        RedisSubscriber redisSubscriber = new RedisSubscriber(redisMessageListenerContainer, sseEmitter);

        redisMessageListenerContainer.addMessageListener(redisSubscriber, new ChannelTopic(userId.toString()));

        sseEmitter.onTimeout(() -> {
            redisMessageListenerContainer.removeMessageListener(redisSubscriber, new ChannelTopic(userId.toString()));
        });

        try {
            sseEmitter.send(
                    SseEmitter.event().id("").name(ALARM_NAME).data("connect completed")
            );
        } catch (IOException e) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }

    public void send(AlarmType type, AlarmArgs args, Integer receiverUserId) {
        UserEntity userEntity = userRepository.findById(receiverUserId).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND));
        AlarmEntity savedAlarm = alarmEntityRepository.save(AlarmEntity.of(userEntity, type, args));

        redisPublisher.publish(new ChannelTopic(receiverUserId.toString()).getTopic(), Alarm.fromEntity(savedAlarm));
    }
}
