package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.dto.SleepAveragesResponse;
import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(SleepLogService.class);

    private static final int AVERAGING_PERIOD_DAYS = 30;
    private static final int SECONDS_IN_DAY = 24 * 60 * 60;
    private static final int NOON_IN_SECONDS = 12 * 60 * 60;

    private final SleepLogRepository repository;

    public SleepLogService(SleepLogRepository repository) {
        this.repository = repository;
    }

    public SleepLog createSleepLog(Integer userId, LocalDate sleepDate, LocalDateTime bedTime, 
                                    LocalDateTime wakeTime, MorningFeeling morningFeeling) {
        LocalDate effectiveDate = sleepDate != null ? sleepDate : LocalDate.now();
        Optional<SleepLog> existing = repository.findByUserIdAndSleepDate(userId, effectiveDate);
        
        SleepLog sleepLog;
        if (existing.isPresent()) {
            sleepLog = existing.get();
            log.info("Updating existing sleep log id={} for userId={} on date={}", 
                    sleepLog.getId(), userId, effectiveDate);
        } else {
            sleepLog = new SleepLog();
            log.info("Creating new sleep log for userId={} on date={}", userId, effectiveDate);
        }

        sleepLog.setUserId(userId);
        sleepLog.setSleepDate(effectiveDate);
        sleepLog.setBedTime(bedTime);
        sleepLog.setWakeTime(wakeTime);
        sleepLog.setMorningFeeling(morningFeeling);

        SleepLog saved = repository.save(sleepLog);
        log.debug("Saved sleep log id={} with totalTimeInBed={} minutes", 
                saved.getId(), saved.getTotalTimeInBedMinutes());
        return saved;
    }

    public Optional<SleepLog> getLastNightSleep(Integer userId) {
        LocalDate today = LocalDate.now();
        log.debug("Looking up sleep log for userId={} on date={}", userId, today);
        Optional<SleepLog> result = repository.findByUserIdAndSleepDate(userId, today);
        if (result.isEmpty()) {
            log.debug("No sleep log found for userId={} on date={}", userId, today);
        }
        return result;
    }

    public Optional<SleepAveragesResponse> getLast30DayAverages(Integer userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(AVERAGING_PERIOD_DAYS - 1);

        log.debug("Fetching sleep logs for userId={} from {} to {}", userId, startDate, endDate);
        List<SleepLog> sleepLogs = repository.findByUserIdAndSleepDateBetween(userId, startDate, endDate);

        if (sleepLogs.isEmpty()) {
            log.info("No sleep logs found for userId={} in period {} to {}", userId, startDate, endDate);
            return Optional.empty();
        }

        log.debug("Found {} sleep logs for userId={}, calculating averages", sleepLogs.size(), userId);
        
        SleepAveragesResponse response = new SleepAveragesResponse(
                startDate,
                endDate,
                sleepLogs.size(),
                calculateAverageTimeInBed(sleepLogs),
                calculateAverageBedTime(sleepLogs),
                calculateAverageWakeTime(sleepLogs),
                createMorningFeelingMapCounter(sleepLogs)
        );
        
        log.info("Calculated averages for userId={}: avgBedTime={}, avgWakeTime={}, avgTimeInBed={} min",
                userId, response.getAverageBedTime(), response.getAverageWakeTime(), response.getAverageTotalTimeInBedMinutes());
        
        return Optional.of(response);
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
