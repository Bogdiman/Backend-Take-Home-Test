package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SleepLogServiceTest {

    @Mock
    private SleepLogRepository repository;

    private SleepLogService service;

    @BeforeEach
    void setUp() {
        service = new SleepLogService(repository);
    }

    @Test
    void createSleepLog_shouldSaveAndReturnSleepLog() {
        LocalDateTime bedTime = LocalDateTime.of(2026, 3, 2, 23, 0);
        LocalDateTime wakeTime = LocalDateTime.of(2026, 3, 3, 7, 30);

        when(repository.findByUserIdAndSleepDate(any(), any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> {
            SleepLog log = invocation.getArgument(0);
            log.setId(1);
            return log;
        });

        SleepLog result = service.createSleepLog(1, bedTime, wakeTime, MorningFeeling.GOOD);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getBedTime()).isEqualTo(bedTime);
        assertThat(result.getWakeTime()).isEqualTo(wakeTime);
        assertThat(result.getMorningFeeling()).isEqualTo(MorningFeeling.GOOD);
        verify(repository).save(any());
    }

    @Test
    void createSleepLog_shouldUpdateExistingLog_whenSameDateExists() {
        LocalDateTime bedTime = LocalDateTime.of(2026, 3, 2, 23, 0);
        LocalDateTime wakeTime = LocalDateTime.of(2026, 3, 3, 7, 30);

        SleepLog existingLog = new SleepLog();
        existingLog.setId(1);
        existingLog.setUserId(1);

        when(repository.findByUserIdAndSleepDate(any(), any())).thenReturn(Optional.of(existingLog));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SleepLog result = service.createSleepLog(1, bedTime, wakeTime, MorningFeeling.OK);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getMorningFeeling()).isEqualTo(MorningFeeling.OK);
        verify(repository).save(existingLog);
    }

    @Test
    void createSleepLog_shouldThrowException_whenWakeTimeBeforeBedTime() {
        LocalDateTime bedTime = LocalDateTime.of(2026, 3, 3, 7, 30);
        LocalDateTime wakeTime = LocalDateTime.of(2026, 3, 2, 23, 0);

        assertThatThrownBy(() -> service.createSleepLog(1, bedTime, wakeTime, MorningFeeling.GOOD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wake time must be after bed time");
    }

    @Test
    void createSleepLog_shouldThrowException_whenWakeTimeEqualsBedTime() {
        LocalDateTime sameTime = LocalDateTime.of(2026, 3, 3, 7, 30);

        assertThatThrownBy(() -> service.createSleepLog(1, sameTime, sameTime, MorningFeeling.GOOD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wake time must be after bed time");
    }

    @Test
    void getLastNightSleep_shouldReturnSleepLog_whenExists() {
        SleepLog sleepLog = new SleepLog(1, LocalDate.now(),
                LocalDateTime.of(2026, 3, 2, 23, 0),
                LocalDateTime.of(2026, 3, 3, 7, 30),
                MorningFeeling.GOOD);
        sleepLog.setId(1);

        when(repository.findByUserIdAndSleepDate(1, LocalDate.now())).thenReturn(Optional.of(sleepLog));

        Optional<SleepLog> result = service.getLastNightSleep(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
    }

    @Test
    void getLastNightSleep_shouldReturnEmpty_whenNotExists() {
        when(repository.findByUserIdAndSleepDate(1, LocalDate.now())).thenReturn(Optional.empty());

        Optional<SleepLog> result = service.getLastNightSleep(1);

        assertThat(result).isEmpty();
    }
}
