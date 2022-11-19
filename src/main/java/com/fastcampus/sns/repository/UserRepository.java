package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final UserEntityRepository userEntityRepository;
    private final UserCacheRepository userCacheRepository;

    public Optional<UserEntity> findByUsername(String username) {
        Optional<UserEntity> cacheUser = userCacheRepository.getUser(username);

        if (cacheUser.isEmpty()) {
            Optional<UserEntity> userEntity = userEntityRepository.findByUsername(username);

            userEntity.ifPresent(user -> {
                userCacheRepository.setUser(user);
            });

            return userEntity;
        } else {
            return cacheUser;
        }
    }

    public UserEntity save(UserEntity userEntity) {
        return userEntityRepository.save(userEntity);
    }
}
