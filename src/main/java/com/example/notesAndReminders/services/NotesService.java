package com.example.notesAndReminders.services;

import com.example.notesAndReminders.dtos.*;
import com.example.notesAndReminders.entities.Note;
import com.example.notesAndReminders.entities.Schedule;
import com.example.notesAndReminders.entities.enums.TypeNote;
import com.example.notesAndReminders.repositories.NotesRepository;
import com.example.notesAndReminders.repositories.SchedulesRepository;
import com.example.notesAndReminders.repositories.UsersRepository;
import com.example.notesAndReminders.scheduler.MailJob;
import com.example.notesAndReminders.scheduler.ScheduleUtils;
import com.example.notesAndReminders.scheduler.TimerInfo;
import com.example.notesAndReminders.security.UserDetails;
import com.example.notesAndReminders.util.exceptions.SchedulerCreateException;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotesService {

    @Value("${spring.host.time_zone_in_ms}")
    private long hostTimeZoneDiff;

    private final NotesRepository notesRepository;

    private final UsersRepository usersRepository;

    private final SchedulesRepository schedulesRepository;

    private final Scheduler scheduler;

    @Autowired
    public NotesService(NotesRepository notesRepository, UsersRepository usersRepository, SchedulesRepository schedulesRepository, Scheduler scheduler) {
        this.notesRepository = notesRepository;
        this.usersRepository = usersRepository;
        this.schedulesRepository = schedulesRepository;
        this.scheduler = scheduler;
    }

    public NoteReadDto findById (Long id) {
        Note note = notesRepository.findByIdWithSchedule(id)
                .orElseThrow(IllegalArgumentException::new);
        return mapFrom(note);
    }

    public List<NoteReadDto> findAllByUserId(Long userId) {
        return notesRepository.findAllByUserIdWithSchedule(userId)
                .stream()
                .map(this::mapFrom)
                .toList();
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
                    calcStartAt(dto.getSchedule().getStartAt(), user.getUser().getTimeZone())
            );

            schedulesRepository.save(schedule);

            createSchedule(user, schedule, note);
        }
    }

    @Transactional
    public void update(Long id, NoteCreateDto dto, UserDetails user) throws SchedulerException {
        Note note = notesRepository.findByIdWithSchedule(id)
                .orElseThrow(IllegalArgumentException::new);
        Schedule schedule = note.getSchedule();
        note.setTitle(dto.getTitle());
        note.setContent(note.getContent());
        note.setTypeNote(dto.getCategory() == null ? TypeNote.DEFAULT : TypeNote.valueOf(dto.getCategory()));
        if (note.getSchedule() != null){
            scheduler.deleteJob(new JobKey(note.getSchedule().getId()));
        }

        if (dto.getSchedule() == null){
            note.setSchedule(null);
        }
        else {
            schedule.setStartAt(calcStartAt(dto.getSchedule().getStartAt(), user.getUser().getTimeZone()));
            schedule.setIsReminded(dto.getSchedule().getIsReminded() != null && dto.getSchedule().getIsReminded());
            schedule.setRepeatCount(dto.getSchedule().getRepeatCount() == null ? 1
                    : dto.getSchedule().getRepeatCount());
            schedule.setRepeatInterval(dto.getSchedule().getRepeatInterval() == null ? 60_000L
                    : dto.getSchedule().getRepeatInterval());

            schedulesRepository.save(schedule);
        }

        notesRepository.save(note);

        if (note.getSchedule() != null) {
            createSchedule(user, schedule, note);
        }
    }

    @Transactional
    public void delete(Long id) throws SchedulerException {
        Note note = notesRepository.findByIdWithSchedule(id)
                .orElseThrow(IllegalArgumentException::new);
        scheduler.deleteJob(new JobKey(note.getSchedule().getId()));
        notesRepository.deleteByIdQuickly(id);
    }

    private void createSchedule(UserDetails user, Schedule schedule, Note note) {
        TimerInfo timerInfo = ScheduleUtils
                .builderTimerInfo(schedule.getId(), user.getUsername(),
                        new NoteReminderDto(note.getId(), note.getTitle(), note.getContent()))
                .startAt(schedule.getStartAt(), hostTimeZoneDiff)
                .sendMail(schedule.getIsReminded(), user.getUser().getEmail())
                .repeatable(schedule.getRepeatCount(), schedule.getRepeatInterval())
                .build();

        JobDetail jobDetail = ScheduleUtils.buildJobDetails(MailJob.class, schedule.getId(), timerInfo);
        Trigger trigger = ScheduleUtils.buildTrigger(schedule.getId(), timerInfo);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new SchedulerCreateException(e.getMessage(), e);
        }
    }

    private long calcStartAt(String date, String timeZone) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.of(timeZone))
                .toEpochSecond() * 1000L;
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

    private NoteReadDto mapFrom(Note entity) {
        return new NoteReadDto(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getTypeNote().toString(),
                mapFrom(entity.getSchedule())
        );
    }

    private ScheduleReadDto mapFrom(Schedule entity) {
        if (entity == null)
            return null;
        return new ScheduleReadDto(
                entity.getId(),
                new Date(entity.getStartAt() - 2 * hostTimeZoneDiff).toString(),
                entity.getIsReminded(),
                entity.getRepeatCount(),
                entity.getRepeatInterval()
        );
    }

}
