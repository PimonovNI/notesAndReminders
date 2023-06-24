package com.example.notesAndReminders.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteReminderDto {
    private Long id;
    private String title;
    private String content;
}
