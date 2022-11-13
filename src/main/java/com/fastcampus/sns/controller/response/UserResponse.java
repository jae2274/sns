package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.User;
import com.fastcampus.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String username;
    private UserRole userRole;

    public static UserResponse fromUser(User user){
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userRole(user.getUserRole())
                .build();
    }
}
