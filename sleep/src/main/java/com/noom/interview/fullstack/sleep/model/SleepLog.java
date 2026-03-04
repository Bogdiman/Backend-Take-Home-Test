package com.noom.interview.fullstack.sleep.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sleep_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SleepLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "sleep_date", nullable = false)
    private LocalDate sleepDate;

    @Column(name = "bed_time", nullable = false)
    private LocalDateTime bedTime;

    @Column(name = "wake_time", nullable = false)
    private LocalDateTime wakeTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "morning_feeling", nullable = false)
    private MorningFeeling morningFeeling;

    public SleepLog(Integer userId, LocalDate sleepDate, LocalDateTime bedTime,
                    LocalDateTime wakeTime, MorningFeeling morningFeeling) {
        this.userId = userId;
        this.sleepDate = sleepDate;
        this.bedTime = bedTime;
        this.wakeTime = wakeTime;
        this.morningFeeling = morningFeeling;
    }

    @Transient
    public Integer getTotalTimeInBedMinutes() {
        if (bedTime == null || wakeTime == null) {
            return null;
        }
        return (int) Duration.between(bedTime, wakeTime).toMinutes();
    }
}
