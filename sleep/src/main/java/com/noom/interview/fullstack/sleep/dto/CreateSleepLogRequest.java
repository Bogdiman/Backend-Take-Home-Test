package com.noom.interview.fullstack.sleep.dto;

import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import com.noom.interview.fullstack.sleep.validation.ValidSleepInterval;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ValidSleepInterval
public class CreateSleepLogRequest {

    @NotNull(message = "Bed time is required")
    public LocalDateTime bedTime;

    @NotNull(message = "Wake time is required")
    public LocalDateTime wakeTime;

    @NotNull(message = "Morning feeling is required")
    public MorningFeeling morningFeeling;
}
