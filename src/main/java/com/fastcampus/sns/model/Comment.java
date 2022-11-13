package com.fastcampus.sns.model;

import com.fastcampus.sns.model.entity.CommentEntity;
import com.fastcampus.sns.model.entity.PostEntity;
import lombok.*;

import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    public static Comment fromEntity(CommentEntity commentEntity){
        return Comment.builder()
                . id(commentEntity.getId())
        .comment(commentEntity.getComment())
        .userName(commentEntity.getUser().getUsername())
        . postId(commentEntity.getPost().getId())
                .registeredAt(commentEntity.getRegisteredAt())
        .updatedAt(commentEntity.getUpdatedAt())
        .deletedAt(commentEntity.getDeletedAt())
                .build();
    }

}
