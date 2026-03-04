package com.noom.interview.fullstack.sleep.dto;

import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SleepAveragesResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private int totalNights;
    private int averageTotalTimeInBedMinutes;
    private LocalTime averageBedTime;
    private LocalTime averageWakeTime;
    private Map<MorningFeeling, Long> morningFeelingFrequencies;
}
