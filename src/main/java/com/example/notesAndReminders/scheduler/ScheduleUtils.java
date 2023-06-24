package com.example.notesAndReminders.scheduler;

import com.example.notesAndReminders.dtos.NoteReminderDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.quartz.*;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleUtils {

    public static JobDetail buildJobDetails(Class<? extends Job> jobClass, String id, TimerInfo timerInfo) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("info", timerInfo);

        return JobBuilder
                .newJob(jobClass)
                .withIdentity(id)
                .usingJobData(jobDataMap)
                .build();
    }

    public static Trigger buildTrigger(String id, TimerInfo timerInfo) {
        SimpleScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule()
                .withRepeatCount(timerInfo.getTotalFireCount())
                .withIntervalInMilliseconds(timerInfo.getRepeatIntervalMs());

        if (timerInfo.getInitOffsetMs() == 0)
            return TriggerBuilder.newTrigger()
                    .withIdentity(id)
                    .startNow()
                    .build();

        return TriggerBuilder.newTrigger()
                .withIdentity(id)
                .withSchedule(scheduleBuilder)
                .startAt(new Date(timerInfo.getInitOffsetMs()))
                .build();
    }

    public static TimerInfoBuilder builderTimerInfo(String jobId, String username, NoteReminderDto note) {
        return new TimerInfoBuilder(jobId, username, note);
    }

    public static class TimerInfoBuilder {

        private final TimerInfo timerInfo;

        private TimerInfoBuilder(String jobId, String username, NoteReminderDto note) {
            timerInfo = new TimerInfo(jobId, username, note);
        }

        public TimerInfoBuilder startAt(Long startAt, Long systemZoneTime) {
            timerInfo.setInitOffsetMs(startAt - 2 * systemZoneTime);
            return this;
        }

        public TimerInfoBuilder sendMail(boolean sendMail) {
            timerInfo.setSendMail(sendMail);
            return this;
        }

        public TimerInfoBuilder sendMail(String email) {
            timerInfo.setSendMail(true);
            timerInfo.setEmail(email);
            return this;
        }

        public TimerInfoBuilder sendMail(boolean sendMail, String email) {
            timerInfo.setSendMail(sendMail);
            timerInfo.setEmail(email);
            return this;
        }

        public TimerInfoBuilder repeatable(int count, long interval) {
            timerInfo.setTotalFireCount(count);
            timerInfo.setRepeatIntervalMs(interval);
            return this;
        }

        public TimerInfo build(){
            return timerInfo;
        }
    }

}
