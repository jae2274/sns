package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.UserRepository;
import com.fastcampus.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUserName(String userName){
        return userRepository.findByUsername(userName)
                .map(userEntity -> User.fromEntity(userEntity))
                .orElseThrow(()->new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found",userName)));
    }

    @Transactional
    public User join(String username, String password){

        userRepository.findByUsername(username).ifPresent(it->{
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME,String.format("%s is duplicated",username));
        });

        UserEntity userEntity = userRepository.save(UserEntity.of(username, passwordEncoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    @Transactional
    public String login(String username, String password){
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(()->new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded",username)));

        if( !passwordEncoder.matches(password, userEntity.getPassword()) )
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);

        return JwtTokenUtils.generateToken(username, secretKey, expiredTimeMs);
    }
}
