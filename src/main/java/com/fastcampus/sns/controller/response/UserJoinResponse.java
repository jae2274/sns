package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.User;
import com.fastcampus.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class UserJoinResponse {
    private Integer id;
    private String username;
    private UserRole userRole;


    public static UserJoinResponse fromUser(User user){
        return UserJoinResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userRole(user.getUserRole())
                .build();
    }
}
