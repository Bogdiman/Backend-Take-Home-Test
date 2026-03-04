package com.noom.interview.fullstack.sleep.validation;

import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;
import java.time.LocalDate;

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

        if (request.getSleepDate() != null) {
            return isValidSleepDateAlignment(request, context);
        }

        return true;
    }

    /***
     * Method that checks if the sleep date aligns with the bed time and wake time. 
     * The wake time must be on the same date as the sleep date, and the bed time must be either on the same date or the day before.
     * @param request
     * @param context
     * @return
     */
    private boolean isValidSleepDateAlignment(CreateSleepLogRequest request, ConstraintValidatorContext context) {
        LocalDate sleepDate = request.getSleepDate();
        LocalDate wakeDate = request.getWakeTime().toLocalDate();
        LocalDate bedDate = request.getBedTime().toLocalDate();

        if (!wakeDate.equals(sleepDate)) {
            log.error("Wake time date {} does not match sleep date {}", wakeDate, sleepDate);
            context.buildConstraintViolationWithTemplate(
                    String.format("Wake time date (%s) must match sleep date (%s)", wakeDate, sleepDate))
                    .addConstraintViolation();
            return false;
        }

        LocalDate dayBeforeSleepDate = sleepDate.minusDays(1);
        if (!bedDate.equals(sleepDate) && !bedDate.equals(dayBeforeSleepDate)) {
            log.error("Bed time date {} is not valid for sleep date {} (expected {} or {})", 
                    bedDate, sleepDate, dayBeforeSleepDate, sleepDate);
            context.buildConstraintViolationWithTemplate(
                    String.format("Bed time date (%s) must be on sleep date (%s) or the day before (%s)", 
                            bedDate, sleepDate, dayBeforeSleepDate))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
