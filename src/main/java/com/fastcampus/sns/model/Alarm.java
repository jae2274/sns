package com.fastcampus.sns.model;

import com.fastcampus.sns.model.entity.AlarmEntity;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Alarm {
    private Integer id;
    private AlarmType alarmType;
    private AlarmArgs args;
    private User user;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Alarm fromEntity(AlarmEntity alarm) {
        return Alarm.builder()
                .id(alarm.getId())
                .alarmType(alarm.getAlarmType())
                .args(alarm.getArgs())
                .user(User.fromEntity(alarm.getUser()))
                .registeredAt(alarm.getRegisteredAt())
                .updatedAt(alarm.getUpdatedAt())
                .deletedAt(alarm.getDeletedAt())
                .build();
    }
}
