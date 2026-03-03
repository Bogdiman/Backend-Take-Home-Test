package com.noom.interview.fullstack.sleep.dto;

import com.noom.interview.fullstack.sleep.model.MorningFeeling;

import java.time.LocalDateTime;

public class CreateSleepLogRequest {
    public LocalDateTime bedTime;
    public LocalDateTime wakeTime;
    public MorningFeeling morningFeeling;
}
