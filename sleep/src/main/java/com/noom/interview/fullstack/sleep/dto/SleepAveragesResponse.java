package com.noom.interview.fullstack.sleep.dto;

import com.noom.interview.fullstack.sleep.model.MorningFeeling;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class SleepAveragesResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private int totalNights;
    private int averageTotalTimeInBedMinutes;
    private LocalTime averageBedTime;
    private LocalTime averageWakeTime;
    private Map<MorningFeeling, Long> morningFeelingFrequencies;

    public SleepAveragesResponse() {}

    public SleepAveragesResponse(LocalDate startDate, LocalDate endDate, int totalNights,
                                  int averageTotalTimeInBedMinutes, LocalTime averageBedTime,
                                  LocalTime averageWakeTime, Map<MorningFeeling, Long> morningFeelingFrequencies) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalNights = totalNights;
        this.averageTotalTimeInBedMinutes = averageTotalTimeInBedMinutes;
        this.averageBedTime = averageBedTime;
        this.averageWakeTime = averageWakeTime;
        this.morningFeelingFrequencies = morningFeelingFrequencies;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getTotalNights() { return totalNights; }
    public void setTotalNights(int totalNights) { this.totalNights = totalNights; }

    public int getAverageTotalTimeInBedMinutes() { return averageTotalTimeInBedMinutes; }
    public void setAverageTotalTimeInBedMinutes(int averageTotalTimeInBedMinutes) { this.averageTotalTimeInBedMinutes = averageTotalTimeInBedMinutes; }

    public LocalTime getAverageBedTime() { return averageBedTime; }
    public void setAverageBedTime(LocalTime averageBedTime) { this.averageBedTime = averageBedTime; }

    public LocalTime getAverageWakeTime() { return averageWakeTime; }
    public void setAverageWakeTime(LocalTime averageWakeTime) { this.averageWakeTime = averageWakeTime; }

    public Map<MorningFeeling, Long> getMorningFeelingFrequencies() { return morningFeelingFrequencies; }
    public void setMorningFeelingFrequencies(Map<MorningFeeling, Long> morningFeelingFrequencies) { this.morningFeelingFrequencies = morningFeelingFrequencies; }
}
