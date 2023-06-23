package com.example.notesAndReminders.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteCreateDto {

    @NotEmpty(message = "Must not be empty")
    private String title;

    @NotEmpty(message = "Must not be empty")
    private String content;

    private String category;

    private ScheduleCreateDto schedule;

}
