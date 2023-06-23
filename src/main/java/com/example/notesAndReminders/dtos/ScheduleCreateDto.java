package com.example.notesAndReminders.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleCreateDto {

    @NotEmpty(message = "Must not be empty")
    @Pattern(regexp = "^\\d{4}-((0\\d)|(1[0-2]))-(([0-2]\\d)|(3[0-1]))T(([0-1]\\d)|(2[0-4])):(([0-5]\\d)|(60)):(([0-5]\\d)|(60))$",
            message = "Illegal format of time, must be: yyyy-MM-ddThh:mm:ss")
    private String startAt;

    private Boolean isReminded;

    @Max(value = 10, message = "Must be less or equals 10")
    @Min(value = 1, message = "Must be greater or equals 1")
    private Integer repeatCount;

    @Max(value = 3_600_000L, message = "Must be less or equals 3600000ms (1 hour)")
    @Min(value = 1L, message = "Must be greater or equals 1")
    private Long repeatInterval;

}
