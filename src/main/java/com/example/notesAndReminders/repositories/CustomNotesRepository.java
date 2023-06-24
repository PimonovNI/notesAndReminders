package com.example.notesAndReminders.repositories;

import com.example.notesAndReminders.entities.Note;

import java.util.Optional;
import java.util.List;

public interface CustomNotesRepository {

    Optional<Note> findByIdWithSchedule(Long id);

    List<Note> findAllByUserIdWithSchedule(Long userId);

}
