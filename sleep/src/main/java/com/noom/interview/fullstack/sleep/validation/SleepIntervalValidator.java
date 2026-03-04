package com.noom.interview.fullstack.sleep.validation;

import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SleepIntervalValidator implements ConstraintValidator<ValidSleepInterval, CreateSleepLogRequest> {

    @Override
    public boolean isValid(CreateSleepLogRequest request, ConstraintValidatorContext context) {
        if (request.bedTime == null || request.wakeTime == null) {
            return true;
        }
        return request.wakeTime.isAfter(request.bedTime);
    }
}
