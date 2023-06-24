package com.example.notesAndReminders.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailUtils {

    public static final String SUBJECT_FOR_REGISTRATION = "Verify your email for website \"Notes and Reminders\"";
    public static final String SUBJECT_FOR_REMINDER = ". Reminder from \"Notes and Reminders\"";
    private static final String urlRegFormat = "http://localhost:8080/api/v1/auth/activation/%s";

    public static String getRegistrationText(String name, String activationCode){
        return String.format("Hi, %s. We are glad to work with you. Your teem hope, this app will be ease to using and helpful. " +
                        "Please, click following URL to complete registration:\n\n" + urlRegFormat,
                name, activationCode);
    }

    public static String getReminderText(String name, String content){
        return String.format("Hi, %s. Your reminder:\n %s", name, content);
    }

}
