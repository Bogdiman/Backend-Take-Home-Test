package com.noom.interview.fullstack.sleep.dto;

import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import com.noom.interview.fullstack.sleep.validation.ValidSleepInterval;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ValidSleepInterval
@Getter
@Setter
public class CreateSleepLogRequest {

    private LocalDate sleepDate;

    @NotNull(message = "Bed time is required")
    private LocalDateTime bedTime;

    @NotNull(message = "Wake time is required")
    private LocalDateTime wakeTime;

    @NotNull(message = "Morning feeling is required")
    private MorningFeeling morningFeeling;
}
