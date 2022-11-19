package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.fixture.UserEntityFixture;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        String username = "username";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encrypt_password");
        when(userRepository.save(any())).thenReturn(UserEntityFixture.get(username, password, 1));


        assertDoesNotThrow(() -> userService.join(username, password));
    }

    @Test
    void 회원가입시_usernam으로_회원가입한_유저가_이미_있는_경우() {
        String username = "username";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(UserEntityFixture.get(username, password, 1)));
        when(passwordEncoder.encode(password)).thenReturn("encrypt_password");
        when(userRepository.save(any())).thenReturn(UserEntityFixture.get(username, password, 1));


        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.join(username, password));
        assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
    }

    @Test
    void 로그인이_정상적으로동작하는_경우() {
        String username = "username";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(username, password, 1);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(fixture));
        when(passwordEncoder.matches(password, fixture.getPassword())).thenReturn(true);

        assertDoesNotThrow(() -> userService.login(username, password));
    }

    @Test
    void 로그인시_usernam으로_회원가입한_유저가_없는_경우() {
        String username = "username";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());


        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.login(username, password));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 로그인시_패스워드가_틀린_경우() {
        String username = "username";
        String password = "password";
        String wrongPassword = "wrongPassword";

        UserEntity fixture = UserEntityFixture.get(username, password, 1);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(fixture));


        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.login(username, wrongPassword));
        assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }
}