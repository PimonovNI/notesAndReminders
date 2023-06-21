package com.example.notesAndReminders.repositories;

import com.example.notesAndReminders.entities.ActivationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationKeysRepositories extends JpaRepository<ActivationKey, Long> {

    Optional<ActivationKey> findByActivationCode(String activationCode);

    @Query("SELECT a FROM ActivationKey a INNER JOIN FETCH User u WHERE a.activationCode = :activationCode")
    Optional<ActivationKey> findByActivationCodeWithUser(@Param("activationCode") String activationCode);

    @Modifying
    @Query("DELETE FROM ActivationKey a WHERE a.id = :id")
    void deleteByIdQuickly(@Param("id") Long id);

}
