package com.example.notesAndReminders.util.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.List;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateFailedResponse {

    private String title;
    private String message;
    private Map<String, List<String>> errors;
    private Instant timestamp;

}
