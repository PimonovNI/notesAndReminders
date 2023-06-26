package com.example.notesAndReminders.services;

import com.example.notesAndReminders.dtos.NoteCreateDto;
import com.example.notesAndReminders.dtos.ScheduleCreateDto;
import com.example.notesAndReminders.entities.Note;
import com.example.notesAndReminders.entities.Schedule;
import com.example.notesAndReminders.entities.User;
import com.example.notesAndReminders.entities.enums.Role;
import com.example.notesAndReminders.entities.enums.Status;
import com.example.notesAndReminders.entities.enums.TypeNote;
import com.example.notesAndReminders.repositories.NotesRepository;
import com.example.notesAndReminders.repositories.SchedulesRepository;
import com.example.notesAndReminders.repositories.UsersRepository;
import com.example.notesAndReminders.scheduler.TimerInfo;
import com.example.notesAndReminders.security.UserDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NotesServiceTest {

    private final NotesService notesService;

    @Autowired
    NotesServiceTest(NotesService notesService) {
        this.notesService = notesService;
    }

    private final long hostTimeZoneDiff = 0L;

    @MockBean
    private NotesRepository notesRepository;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private SchedulesRepository schedulesRepository;

    @MockBean
    private Scheduler scheduler;

    @MockBean
    private UserDetails userDetails;

    @Test
    void saveTest1() throws SchedulerException {
        String title = "TITLE";
        String content = "Very big message";
        String email = "test@gmail.com";
        User user = User.builder()
                .id(1L)
                .username("test1")
                .email(email)
                .status(Status.ACTIVE)
                .role(Role.USER)
                .timeZone("GMT+03:00")
                .build();
        NoteCreateDto dto = new NoteCreateDto(
                title,
                content,
                "METING",
                new ScheduleCreateDto(
                        "2023-06-24T15:32:10",
                        true,
                        2,
                        180_000L
                )
        );

        Mockito.doReturn(user)
                .when(userDetails)
                .getUser();
        Mockito.doReturn(user)
                .when(usersRepository)
                .getReferenceById(ArgumentMatchers.anyLong());

        notesService.save(dto, userDetails);

        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        ArgumentCaptor<Schedule> scheduleCaptor = ArgumentCaptor.forClass(Schedule.class);
        ArgumentCaptor<JobDetail> jobDetailCaptor = ArgumentCaptor.forClass(JobDetail.class);
        ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);

        Mockito.verify(notesRepository, Mockito.times(1))
                .save(noteCaptor.capture());
        Mockito.verify(schedulesRepository, Mockito.times(1))
                .save(scheduleCaptor.capture());
        Mockito.verify(scheduler, Mockito.times(1))
                .scheduleJob(jobDetailCaptor.capture(), triggerCaptor.capture());

        Note note = noteCaptor.getValue();
        Schedule schedule = scheduleCaptor.getValue();
        JobDetail jobDetail = jobDetailCaptor.getValue();
        Trigger trigger = triggerCaptor.getValue();

        Assertions.assertEquals(title, note.getTitle());
        Assertions.assertEquals(content, note.getContent());
        Assertions.assertEquals(TypeNote.METING, note.getTypeNote());

        Assertions.assertEquals(note, schedule.getNote());
        Assertions.assertEquals(1687609930000L, schedule.getStartAt());

        String scheduleId = schedule.getId();

        Assertions.assertTrue(jobDetail.getKey().toString().endsWith("." + scheduleId));
        Assertions.assertTrue(trigger.getKey().toString().endsWith("." + scheduleId));

        TimerInfo timerInfo = (TimerInfo) jobDetail.getJobDataMap().get("info");

        Assertions.assertEquals(email, timerInfo.getEmail());
        Assertions.assertEquals(1687588330000L, timerInfo.getInitOffsetMs());
        Assertions.assertEquals(scheduleId, timerInfo.getJobId());

    }
}