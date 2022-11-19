package com.fastcampus.sns.config;

import com.fastcampus.sns.model.entity.UserEntity;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    @Bean

    public RedisConnectionFactory redisConnectionFactory() {
        RedisURI redisURI = RedisURI.create(redisProperties.getUrl());

        org.springframework.data.redis.connection.RedisConfiguration configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI);

        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);

        factory.afterPropertiesSet();

        return factory;
    }

    @Bean
    public RedisTemplate<String, UserEntity> userRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserEntity> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(
                connectionFactory
        );
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<UserEntity>(UserEntity.class));

        return redisTemplate;
    }
}
