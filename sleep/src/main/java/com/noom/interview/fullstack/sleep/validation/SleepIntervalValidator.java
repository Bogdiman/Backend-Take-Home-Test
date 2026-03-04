package com.noom.interview.fullstack.sleep.validation;

import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SleepIntervalValidator implements ConstraintValidator<ValidSleepInterval, CreateSleepLogRequest> {

    private static final Logger log = LoggerFactory.getLogger(SleepIntervalValidator.class);

    @Override
    public boolean isValid(CreateSleepLogRequest request, ConstraintValidatorContext context) {
        if (request.getBedTime() == null || request.getWakeTime() == null) {
            return true;
        }
        boolean valid = request.getWakeTime().isAfter(request.getBedTime());
        if (!valid) {
            log.warn("Invalid sleep interval: wakeTime={} is not after bedTime={}", 
                    request.getWakeTime(), request.getBedTime());
        }
        return valid;
    }
}
