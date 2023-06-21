package com.example.notesAndReminders.util.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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
