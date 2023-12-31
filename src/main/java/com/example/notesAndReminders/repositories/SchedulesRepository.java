package com.example.notesAndReminders.repositories;

import com.example.notesAndReminders.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulesRepository extends JpaRepository<Schedule, String> {
}
