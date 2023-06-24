package com.example.notesAndReminders.util;

import com.example.notesAndReminders.entities.Note;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraphUtils {

    public static EntityGraph<Note> graphForNoteFetchSchedule(EntityManager entityManager) {
        EntityGraph<Note> graph = entityManager.createEntityGraph(Note.class);
        graph.addSubgraph("schedule");
        return graph;
    }

}
