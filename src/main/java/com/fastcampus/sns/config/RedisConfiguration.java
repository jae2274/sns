package com.fastcampus.sns.config;

import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.model.entity.UserEntity;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;
    public static final String USER_REDIS_TEMPLATE = "userRedisTemplate";
    public static final String ALARM_REDIS_TEMPLATE = "alarmRedisTemplate";

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisURI redisURI = RedisURI.create(redisProperties.getUrl());

        org.springframework.data.redis.connection.RedisConfiguration configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI);

        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);

        factory.afterPropertiesSet();

        return factory;
    }

    @Bean(name = USER_REDIS_TEMPLATE)
    public RedisTemplate<String, UserEntity> userRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserEntity> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(
                connectionFactory
        );
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<UserEntity>(UserEntity.class));

        return redisTemplate;
    }

    @Bean(name = ALARM_REDIS_TEMPLATE)
    public RedisTemplate<String, Alarm> alarmRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Alarm> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(
                connectionFactory
        );
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Alarm>(Alarm.class));

        return redisTemplate;
    }

    // redis를 경청하고 있다가 메시지 발행(publish)이 오면 Listener가 처리합니다.
    @Bean
    public RedisMessageListenerContainer RedisMessageListener(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
