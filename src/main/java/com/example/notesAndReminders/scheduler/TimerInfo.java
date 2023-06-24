package com.example.notesAndReminders.scheduler;

import com.example.notesAndReminders.dtos.NoteReminderDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimerInfo {
    private long initOffsetMs;
    private boolean sendMail;
    private int totalFireCount;
    private long repeatIntervalMs;
    private String username;
    private String jobId;
    private String email;
    private NoteReminderDto note;

    public TimerInfo(String jobId, String username, NoteReminderDto note) {
        this.jobId = jobId;
        this.note = note;
        this.username = username;
        this.initOffsetMs = 0;
        this.sendMail = false;
        this.repeatIntervalMs = 0;
        this.totalFireCount = 1;
    }

}
