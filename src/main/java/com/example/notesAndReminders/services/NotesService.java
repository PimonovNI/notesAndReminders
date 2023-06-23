package com.example.notesAndReminders.services;

import com.example.notesAndReminders.dtos.NoteCreateDto;
import com.example.notesAndReminders.dtos.ScheduleCreateDto;
import com.example.notesAndReminders.entities.Note;
import com.example.notesAndReminders.entities.Schedule;
import com.example.notesAndReminders.entities.enums.TypeNote;
import com.example.notesAndReminders.repositories.NotesRepository;
import com.example.notesAndReminders.repositories.SchedulesRepository;
import com.example.notesAndReminders.repositories.UsersRepository;
import com.example.notesAndReminders.security.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class NotesService {

    private final NotesRepository notesRepository;

    private final UsersRepository usersRepository;

    private final SchedulesRepository schedulesRepository;

    @Autowired
    public NotesService(NotesRepository notesRepository, UsersRepository usersRepository, SchedulesRepository schedulesRepository) {
        this.notesRepository = notesRepository;
        this.usersRepository = usersRepository;
        this.schedulesRepository = schedulesRepository;
    }

    @Transactional
    public void save(NoteCreateDto dto, UserDetails user) {
        Note note = mapFrom(dto);
        note.setUser(usersRepository.getReferenceById(user.getUser().getId()));
        if (dto.getCategory() != null) {
            note.setTypeNote(TypeNote.valueOf(dto.getCategory()));
        }
        else {
            note.setTypeNote(TypeNote.DEFAULT);
        }

        notesRepository.save(note);

        if (dto.getSchedule() != null) {
            Schedule schedule = mapFrom(dto.getSchedule());
            schedule.setNote(note);
            schedule.setStartAt(
                    LocalDateTime.parse(dto.getSchedule().getStartAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            .atZone(ZoneId.of(user.getUser().getTimeZone()))
                            .toEpochSecond() * 1000L
            );

            schedulesRepository.save(schedule);
        }
    }

    private Note mapFrom(NoteCreateDto dto) {
        return Note.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
    }

    private Schedule mapFrom(ScheduleCreateDto dto) {
        return Schedule.builder()
                .id(UUID.randomUUID().toString())
                .isReminded(dto.getIsReminded() != null && dto.getIsReminded())
                .repeatCount(dto.getRepeatCount() == null ? 1 : dto.getRepeatCount())
                .repeatInterval(dto.getRepeatInterval() == null ? 60_000L : dto.getRepeatInterval())
                .build();
    }

}
