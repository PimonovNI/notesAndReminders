package com.example.notesAndReminders.repositories.impl;

import com.example.notesAndReminders.entities.Note;
import com.example.notesAndReminders.repositories.CustomNotesRepository;
import com.example.notesAndReminders.util.GraphUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.*;
import org.hibernate.graph.GraphSemantic;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomNotesRepositoryImpl implements CustomNotesRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public CustomNotesRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<Note> findByIdWithSchedule(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Map <String, Object> properties = Map.of(
                GraphSemantic.LOAD.getJakartaHintName(), GraphUtils.graphForNoteFetchSchedule(entityManager)
        );
        return Optional.ofNullable(entityManager.find(Note.class, id, properties));
    }

    @Override
    public List<Note> findAllByUserIdWithSchedule(Long userId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Note> criteria = cb.createQuery(Note.class);

        Root<Note> note = criteria.from(Note.class);
        Join<Object, Object> user = note.join("user", JoinType.INNER);
        note.fetch("schedule", JoinType.LEFT);

        criteria.select(note)
                .where(cb.equal(user.get("id"), userId));

        return entityManager.createQuery(criteria)
                .getResultList();
    }
}
