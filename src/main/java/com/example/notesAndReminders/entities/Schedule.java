package com.example.notesAndReminders.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "schedules")
public class Schedule {

    /*This ID is also name JobDetail and Trigger in Quartz Schedule. Generate harding using UUID*/
    @Id
    @Column(name = "schedule_id")
    private String id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @Column(name = "start_at", nullable = false)
    private Long startAt;

    /* Must server send email with reminder */
    @Column(name = "is_reminded", nullable = false)
    private Boolean isReminded;

    @Column(name = "repeat_count")
    private Integer repeatCount;

    /*Interval between sending email in milliseconds*/
    @Column(name = "repeat_interval")
    private Long repeatInterval;

}
