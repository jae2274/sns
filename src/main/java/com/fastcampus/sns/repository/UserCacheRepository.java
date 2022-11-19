package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {
    private final RedisTemplate<String, UserEntity> userRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setUser(UserEntity user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {}, {}", key, user);
        userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);

    }

    public Optional<UserEntity> getUser(String uesrName) {
        String key = getKey(uesrName);
        UserEntity user = userRedisTemplate.opsForValue().get(key);

        log.info("Get User to Redis {}, {}", key, user);

        return Optional.ofNullable(user);
    }

    private String getKey(String userName) {
        return "USER:" + userName;
    }
}
