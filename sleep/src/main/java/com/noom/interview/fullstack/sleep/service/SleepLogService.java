package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.dto.SleepAveragesResponse;
import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SleepLogService {

    private static final int AVERAGING_PERIOD_DAYS = 30;
    private static final int SECONDS_IN_DAY = 24 * 60 * 60;
    private static final int NOON_IN_SECONDS = 12 * 60 * 60;

    private final SleepLogRepository repository;

    public SleepLogService(SleepLogRepository repository) {
        this.repository = repository;
    }

    public SleepLog createSleepLog(Integer userId, LocalDateTime bedTime, 
                                    LocalDateTime wakeTime, MorningFeeling morningFeeling) {
        LocalDate today = LocalDate.now();
        SleepLog sleepLog = repository.findByUserIdAndSleepDate(userId, today)
                .orElse(new SleepLog());

        sleepLog.setUserId(userId);
        sleepLog.setSleepDate(today);
        sleepLog.setBedTime(bedTime);
        sleepLog.setWakeTime(wakeTime);
        sleepLog.setMorningFeeling(morningFeeling);

        return repository.save(sleepLog);
    }

    public Optional<SleepLog> getLastNightSleep(Integer userId) {
        LocalDate today = LocalDate.now();
        return repository.findByUserIdAndSleepDate(userId, today);
    }

    public Optional<SleepAveragesResponse> getLast30DayAverages(Integer userId) {
        LocalDate endDate = LocalDate.now();
        // 30th day is today, so we subtract 29 days to get the start date
        LocalDate startDate = endDate.minusDays(AVERAGING_PERIOD_DAYS - 1);

        List<SleepLog> sleepLogs = repository.findByUserIdAndSleepDateBetween(userId, startDate, endDate);

        if (sleepLogs.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new SleepAveragesResponse(
                startDate,
                endDate,
                sleepLogs.size(),
                calculateAverageTimeInBed(sleepLogs),
                calculateAverageBedTime(sleepLogs),
                calculateAverageWakeTime(sleepLogs),
                createMorningFeelingMapCounter(sleepLogs)
        ));
    }

    /***
     * Calculates the average total time in bed in minutes from a list of sleep logs.
     * @param sleepLogs
     * @return
     */
    private int calculateAverageTimeInBed(List<SleepLog> sleepLogs) {
        return (int) sleepLogs.stream()
                .mapToInt(SleepLog::getTotalTimeInBedMinutes)
                .average()
                .orElse(0);
    }
    
    private LocalTime calculateAverageBedTime(List<SleepLog> sleepLogs) {
        List<LocalTime> bedTimes = sleepLogs.stream()
                .map(log -> log.getBedTime().toLocalTime())
                .collect(Collectors.toList());
        return calculateAverageTime(bedTimes);
    }

    private LocalTime calculateAverageWakeTime(List<SleepLog> sleepLogs) {
        List<LocalTime> wakeTimes = sleepLogs.stream()
                .map(log -> log.getWakeTime().toLocalTime())
                .collect(Collectors.toList());
        return calculateAverageTime(wakeTimes);
    }

    /***
     * Creates a map counting the occurrences of each MorningFeeling in the list of sleep logs.
     * @param sleepLogs
     * @return
     */
    private Map<MorningFeeling, Long> createMorningFeelingMapCounter(List<SleepLog> sleepLogs) {
        return Arrays.stream(MorningFeeling.values())
                .collect(Collectors.toMap(
                        feeling -> feeling,
                        feeling -> sleepLogs.stream()
                                .filter(log -> log.getMorningFeeling() == feeling)
                                .count()
                ));
    }

    /**
     * Calculates the average of a list of times.
     * 
     * For sleep times, we want to treat times before noon as "next day" to correctly average times that span midnight.
     */
    private LocalTime calculateAverageTime(List<LocalTime> times) {
        if (times.isEmpty()) {
            return LocalTime.MIDNIGHT;
        }

        long totalSeconds = times.stream()
                .mapToLong(this::toSleepNormalizedSeconds)
                .sum();

        long averageSeconds = totalSeconds / times.size();
        long normalizedSeconds = averageSeconds % SECONDS_IN_DAY;

        return LocalTime.ofSecondOfDay(normalizedSeconds);
    }

    /**
     * Converts a time to seconds, shifting times before noon by 24 hours.
     * This groups evening times (e.g. 22:00) and early morning times (e.g. 01:00)
     * into a continuous range for correct averaging.
     */
    private long toSleepNormalizedSeconds(LocalTime time) {
        long seconds = time.toSecondOfDay();
        boolean isBeforeNoon = seconds < NOON_IN_SECONDS;
        return isBeforeNoon ? seconds + SECONDS_IN_DAY : seconds;
    }
}
