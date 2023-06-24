package com.example.notesAndReminders.scheduler;

import com.example.notesAndReminders.repositories.NotesRepository;
import com.example.notesAndReminders.services.MailSenderService;
import com.example.notesAndReminders.util.MailUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MailJob implements Job {

    private final NotesRepository notesRepository;

    private final MailSenderService mailSenderService;

    @Autowired
    public MailJob(NotesRepository notesRepository, MailSenderService mailSenderService) {
        this.notesRepository = notesRepository;
        this.mailSenderService = mailSenderService;
    }

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        TimerInfo timerInfo = (TimerInfo) data.get("info");

        notesRepository.deleteByIdQuickly(timerInfo.getNote().getId());

        if (timerInfo.isSendMail()) {
            mailSenderService.send(
                    timerInfo.getEmail(),
                    timerInfo.getNote().getTitle() + MailUtils.SUBJECT_FOR_REMINDER,
                    MailUtils.getReminderText(timerInfo.getUsername(), timerInfo.getNote().getContent())
            );
        }
    }
}
