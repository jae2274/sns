package com.fastcampus.sns.fixture;

import com.fastcampus.sns.model.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String username, String password, Integer userId){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        return userEntity;
    }
}
