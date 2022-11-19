package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.AlarmEntityRepository;
import com.fastcampus.sns.repository.UserCacheRepository;
import com.fastcampus.sns.repository.UserRepository;
import com.fastcampus.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AlarmEntityRepository alarmEntityRepository;
    private final UserCacheRepository userCacheRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUserName(String userName) {
        Optional<User> userCache = userCacheRepository.getUser(userName);

        return userCache.orElseGet(() -> {
            User user = userRepository.findByUsername(userName)
                    .map(userEntity -> User.fromEntity(userEntity))
                    .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));

            userCacheRepository.setUser(user);
            return user;
        });

    }

    @Transactional
    public User join(String username, String password) {

        userRepository.findByUsername(username).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", username));
        });

        UserEntity userEntity = userRepository.save(UserEntity.of(username, passwordEncoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    @Transactional
    public String login(String username, String password) {
        User user = loadUserByUserName(username);

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);

        return JwtTokenUtils.generateToken(username, secretKey, expiredTimeMs);
    }

    public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
//        UserEntity userEntity = userRepository.findByUsername(userName).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        return alarmEntityRepository.findAllByUserId(userId, pageable).map(Alarm::fromEntity);

//        return alarmEntityRepository.findAllByUser(userEntity, pageable).map(Alarm::fromEntity);
    }
}
