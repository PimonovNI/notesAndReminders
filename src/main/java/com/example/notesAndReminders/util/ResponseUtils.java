package com.example.notesAndReminders.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtils {

    public static Map<String, List<String>> buildValidateError(Errors errors) {
        Map<String, List<String>> info = new HashMap<>();

        errors.getFieldErrors()
                .forEach(fieldError -> {
                    if (info.containsKey(fieldError.getField()))
                        info.get(fieldError.getField()).add(fieldError.getDefaultMessage());
                    else
                        info.put(fieldError.getField(), new ArrayList<>(Collections.singletonList(fieldError.getDefaultMessage())));
                });

        return info;
    }

}
