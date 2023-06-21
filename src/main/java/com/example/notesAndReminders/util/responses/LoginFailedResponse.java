package com.example.notesAndReminders.util.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginFailedResponse {

    private String title;
    private String message;
    private Instant timestamp;

}
