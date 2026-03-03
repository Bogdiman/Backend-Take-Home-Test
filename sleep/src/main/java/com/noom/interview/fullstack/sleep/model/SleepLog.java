package com.noom.interview.fullstack.sleep.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sleep_log")
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

    @Column(name = "total_time_in_bed_minutes", nullable = false)
    private Integer totalTimeInBedMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "morning_feeling", nullable = false)
    private MorningFeeling morningFeeling;

    public SleepLog() {}

    public SleepLog(Integer userId, LocalDate sleepDate, LocalDateTime bedTime, 
                    LocalDateTime wakeTime, Integer totalTimeInBedMinutes, 
                    MorningFeeling morningFeeling) {
        this.userId = userId;
        this.sleepDate = sleepDate;
        this.bedTime = bedTime;
        this.wakeTime = wakeTime;
        this.totalTimeInBedMinutes = totalTimeInBedMinutes;
        this.morningFeeling = morningFeeling;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDate getSleepDate() { return sleepDate; }
    public void setSleepDate(LocalDate sleepDate) { this.sleepDate = sleepDate; }

    public LocalDateTime getBedTime() { return bedTime; }
    public void setBedTime(LocalDateTime bedTime) { this.bedTime = bedTime; }

    public LocalDateTime getWakeTime() { return wakeTime; }
    public void setWakeTime(LocalDateTime wakeTime) { this.wakeTime = wakeTime; }

    public Integer getTotalTimeInBedMinutes() { return totalTimeInBedMinutes; }
    public void setTotalTimeInBedMinutes(Integer totalTimeInBedMinutes) { this.totalTimeInBedMinutes = totalTimeInBedMinutes; }

    public MorningFeeling getMorningFeeling() { return morningFeeling; }
    public void setMorningFeeling(MorningFeeling morningFeeling) { this.morningFeeling = morningFeeling; }
}
