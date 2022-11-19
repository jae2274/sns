package com.fastcampus.sns.service;

import com.fastcampus.sns.config.RedisConfiguration;
import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.model.entity.UserEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisPublisher {
    @Resource(name = RedisConfiguration.ALARM_REDIS_TEMPLATE)
    private RedisTemplate<String, UserEntity> alarmRedisTemplate;

    public void publish(String topic, Alarm alarm) {
        alarmRedisTemplate.convertAndSend(topic, alarm);
    }
}
