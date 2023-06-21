package com.example.notesAndReminders.entities;

import com.example.notesAndReminders.entities.enums.TypeNote;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "type", nullable = false, length = 16)
    private TypeNote typeNote;

    @Builder.Default
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "note")
    private Schedule schedule = null;

}
