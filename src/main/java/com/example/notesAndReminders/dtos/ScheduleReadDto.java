package com.example.notesAndReminders.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleReadDto {

    private String id;
    private String startAt;
    private Boolean isReminded;
    private Integer repeatCount;
    private Long repeatInterval;

}
