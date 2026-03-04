package com.noom.interview.fullstack.sleep.validation;

import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

public class SleepIntervalValidator implements ConstraintValidator<ValidSleepInterval, CreateSleepLogRequest> {

    private static final Logger log = LoggerFactory.getLogger(SleepIntervalValidator.class);

    private int minMinutes;
    private int maxMinutes;

    @Override
    public void initialize(ValidSleepInterval constraintAnnotation) {
        this.minMinutes = constraintAnnotation.minMinutes();
        this.maxMinutes = constraintAnnotation.maxMinutes();
    }

    @Override
    public boolean isValid(CreateSleepLogRequest request, ConstraintValidatorContext context) {
        if (request.getBedTime() == null || request.getWakeTime() == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (!request.getWakeTime().isAfter(request.getBedTime())) {
            log.error("Invalid sleep interval: wakeTime={} is not after bedTime={}", 
                    request.getWakeTime(), request.getBedTime());
            context.buildConstraintViolationWithTemplate("Wake time must be after bed time")
                    .addConstraintViolation();
            return false;
        }

        long durationMinutes = Duration.between(request.getBedTime(), request.getWakeTime()).toMinutes();

        if (durationMinutes < minMinutes) {
            log.error("Sleep duration too short: {} minutes (minimum: {} minutes)", durationMinutes, minMinutes);
            context.buildConstraintViolationWithTemplate(
                    String.format("Sleep duration must be at least %d minutes (got %d minutes)", minMinutes, durationMinutes))
                    .addConstraintViolation();
            return false;
        }

        if (durationMinutes > maxMinutes) {
            log.error("Sleep duration too long: {} minutes (maximum: {} minutes)", durationMinutes, maxMinutes);
            context.buildConstraintViolationWithTemplate(
                    String.format("Sleep duration must not exceed %d minutes (got %d minutes)", maxMinutes, durationMinutes))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
