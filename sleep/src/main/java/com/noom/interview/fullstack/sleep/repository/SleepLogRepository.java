package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.model.SleepLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SleepLogRepository extends JpaRepository<SleepLog, Integer> {
    Optional<SleepLog> findByUserIdAndSleepDate(Integer userId, LocalDate sleepDate);
}
