package com.example.notesAndReminders.util.validators;

import com.example.notesAndReminders.dtos.NoteCreateDto;
import com.example.notesAndReminders.entities.enums.TypeNote;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;

@Component
public class NotesValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return NoteCreateDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NoteCreateDto note = (NoteCreateDto) target;

        if (note.getCategory() != null) {
            try {
                TypeNote.valueOf(note.getCategory());
            }
            catch (IllegalArgumentException e) {
                errors.rejectValue("category", "",
                        "Illegal category. Current type of categories: " + Arrays.toString(TypeNote.values()));
            }
        }
    }
}
