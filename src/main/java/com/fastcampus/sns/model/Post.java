package com.fastcampus.sns.model;

import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import lombok.*;

import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    private Integer id;
    private String title;
    private String body;
    private User user;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    public static Post fromEntity(PostEntity postEntity){
        return Post.builder()
                .id(postEntity.getId())
                .title(postEntity.getTitle())
                .body(postEntity.getBody())
                .user(User.fromEntity(postEntity.getUser()))
                .registeredAt(postEntity.getRegisteredAt())
                .updatedAt(postEntity.getUpdatedAt())
                .deletedAt(postEntity.getDeletedAt())
                .build();
    }

}
