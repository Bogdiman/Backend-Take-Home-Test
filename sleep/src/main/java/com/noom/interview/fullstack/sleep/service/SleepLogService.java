package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SleepLogService {

    private final SleepLogRepository repository;

    public SleepLogService(SleepLogRepository repository) {
        this.repository = repository;
    }

    public SleepLog createSleepLog(Integer userId, LocalDateTime bedTime, 
                                    LocalDateTime wakeTime, MorningFeeling morningFeeling) {
        int totalMinutes = (int) Duration.between(bedTime, wakeTime).toMinutes();
        if (totalMinutes <= 0) {
            throw new IllegalArgumentException("Wake time must be after bed time");
        }

        LocalDate today = LocalDate.now();
        SleepLog sleepLog = repository.findByUserIdAndSleepDate(userId, today)
                .orElse(new SleepLog());

        sleepLog.setUserId(userId);
        sleepLog.setSleepDate(today);
        sleepLog.setBedTime(bedTime);
        sleepLog.setWakeTime(wakeTime);
        sleepLog.setTotalTimeInBedMinutes(totalMinutes);
        sleepLog.setMorningFeeling(morningFeeling);

        return repository.save(sleepLog);
    }
}
