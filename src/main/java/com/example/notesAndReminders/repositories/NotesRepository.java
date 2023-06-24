package com.example.notesAndReminders.repositories;

import com.example.notesAndReminders.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotesRepository extends JpaRepository<Note, Long> {

    @Modifying
    @Query("DELETE FROM Note n WHERE n.id = :id")
    void deleteByIdQuickly(@Param("id") Long id);

}
