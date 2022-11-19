package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.model.AlarmArgs;
import com.fastcampus.sns.model.AlarmType;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmResponse {
    private Integer id;
    //    private UserResponse user;
    private AlarmType alarmType;
    private AlarmArgs alarmArgs;
    private String text;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static AlarmResponse fromAlarm(Alarm alarm) {
        return AlarmResponse.builder()
                .id(alarm.getId())
//                .user(UserResponse.fromUser(alarm.getUser()))
                .alarmType(alarm.getAlarmType())
                .alarmArgs(alarm.getArgs())
                .text(alarm.getAlarmType().getAlarmText())
                .registeredAt(alarm.getRegisteredAt())
                .updatedAt(alarm.getUpdatedAt())
                .deletedAt(alarm.getDeletedAt())
                .build();
    }
}
