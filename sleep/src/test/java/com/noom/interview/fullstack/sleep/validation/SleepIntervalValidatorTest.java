package com.noom.interview.fullstack.sleep.validation;

import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;
import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SleepIntervalValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenSleepDurationIsExactlyMinimum() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 3, 7, 0),
                LocalDateTime.of(2026, 3, 3, 7, 30)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_whenSleepDurationIsJustBelowMinimum() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 3, 7, 0),
                LocalDateTime.of(2026, 3, 3, 7, 29)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Sleep duration must be at least 30 minutes");
    }

    @Test
    void shouldPassValidation_whenSleepDurationIsExactlyMaximum() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 2, 0, 0),
                LocalDateTime.of(2026, 3, 3, 0, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_whenSleepDurationIsJustAboveMaximum() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 2, 0, 0),
                LocalDateTime.of(2026, 3, 3, 0, 1)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Sleep duration must not exceed 1440 minutes");
    }

    @Test
    void shouldFailValidation_whenWakeTimeEqualsBeadTime() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 3, 7, 0),
                LocalDateTime.of(2026, 3, 3, 7, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Wake time must be after bed time");
    }

    @Test
    void shouldFailValidation_whenWakeTimeIsBeforeBedTime() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 3, 8, 0),
                LocalDateTime.of(2026, 3, 3, 7, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Wake time must be after bed time");
    }

    @Test
    void shouldPassValidation_whenBedTimeIsNull() {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.setBedTime(null);
        request.setWakeTime(LocalDateTime.of(2026, 3, 3, 7, 0));
        request.setMorningFeeling(MorningFeeling.GOOD);

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .doesNotContain("Wake time must be after bed time")
                .doesNotContain("Sleep duration must be at least");
    }

    @Test
    void shouldPassValidation_whenWakeTimeIsNull() {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.setBedTime(LocalDateTime.of(2026, 3, 3, 23, 0));
        request.setWakeTime(null);
        request.setMorningFeeling(MorningFeeling.GOOD);

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .doesNotContain("Wake time must be after bed time")
                .doesNotContain("Sleep duration must be at least");
    }

    @ParameterizedTest
    @CsvSource({
            "30, true",
            "60, true",
            "480, true",
            "720, true",
            "1440, true",
            "29, false",
            "1, false",
            "1441, false",
            "2880, false"
    })
    void shouldValidateSleepDuration(int durationMinutes, boolean expectedValid) {
        LocalDateTime bedTime = LocalDateTime.of(2026, 3, 3, 0, 0);
        LocalDateTime wakeTime = bedTime.plusMinutes(durationMinutes);
        CreateSleepLogRequest request = createRequest(bedTime, wakeTime);

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        if (expectedValid) {
            assertThat(violations).isEmpty();
        } else {
            assertThat(violations).isNotEmpty();
        }
    }

    @Test
    void shouldPassValidation_forTypicalOvernightSleep() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 2, 23, 0),
                LocalDateTime.of(2026, 3, 3, 7, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidation_forShortNap() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 3, 14, 0),
                LocalDateTime.of(2026, 3, 3, 14, 45)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidation_forLongSleep() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 2, 20, 0),
                LocalDateTime.of(2026, 3, 3, 10, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    // Sleep Date Alignment Tests

    @Test
    void shouldPassValidation_whenSleepDateMatchesWakeTimeAndBedTimeIsDayBefore() {
        CreateSleepLogRequest request = createRequestWithSleepDate(
                LocalDate.of(2026, 3, 4),
                LocalDateTime.of(2026, 3, 3, 23, 0),
                LocalDateTime.of(2026, 3, 4, 7, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidation_whenSleepDateMatchesWakeTimeAndBedTimeIsSameDay() {
        CreateSleepLogRequest request = createRequestWithSleepDate(
                LocalDate.of(2026, 3, 4),
                LocalDateTime.of(2026, 3, 4, 0, 30),
                LocalDateTime.of(2026, 3, 4, 8, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_whenWakeTimeDateDoesNotMatchSleepDate() {
        CreateSleepLogRequest request = createRequestWithSleepDate(
                LocalDate.of(2026, 3, 4),
                LocalDateTime.of(2026, 3, 4, 23, 0),
                LocalDateTime.of(2026, 3, 5, 7, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Wake time date")
                .contains("must match sleep date");
    }

    @Test
    void shouldFailValidation_whenWakeTimeDateIsDayBeforeSleepDate() {
        CreateSleepLogRequest request = createRequestWithSleepDate(
                LocalDate.of(2026, 3, 5),
                LocalDateTime.of(2026, 3, 3, 23, 0),
                LocalDateTime.of(2026, 3, 4, 7, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Wake time date")
                .contains("must match sleep date");
    }

    @Test
    void shouldFailValidation_whenBedTimeDateIsAfterSleepDate() {
        CreateSleepLogRequest request = createRequestWithSleepDate(
                LocalDate.of(2026, 3, 4),
                LocalDateTime.of(2026, 3, 5, 0, 0),
                LocalDateTime.of(2026, 3, 4, 8, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .anySatisfy(msg -> assertThat(msg).contains("Wake time must be after bed time"));
    }

    @Test
    void shouldFailValidation_whenBedTimeDateIsTwoDaysBeforeSleepDate_dueToDurationLimit() {
        CreateSleepLogRequest request = createRequestWithSleepDate(
                LocalDate.of(2026, 3, 4),
                LocalDateTime.of(2026, 3, 2, 7, 0),
                LocalDateTime.of(2026, 3, 4, 7, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .anySatisfy(msg -> assertThat(msg).contains("Sleep duration must not exceed"));
    }

    @Test
    void shouldSkipSleepDateValidation_whenSleepDateIsNull() {
        CreateSleepLogRequest request = createRequest(
                LocalDateTime.of(2026, 3, 2, 23, 0),
                LocalDateTime.of(2026, 3, 3, 7, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidation_forTypicalOvernightSleepWithSleepDate() {
        CreateSleepLogRequest request = createRequestWithSleepDate(
                LocalDate.of(2026, 3, 4),
                LocalDateTime.of(2026, 3, 3, 22, 30),
                LocalDateTime.of(2026, 3, 4, 6, 30)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidation_forEarlyMorningSleepWithSleepDate() {
        CreateSleepLogRequest request = createRequestWithSleepDate(
                LocalDate.of(2026, 3, 4),
                LocalDateTime.of(2026, 3, 4, 1, 0),
                LocalDateTime.of(2026, 3, 4, 9, 0)
        );

        Set<ConstraintViolation<CreateSleepLogRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    private CreateSleepLogRequest createRequest(LocalDateTime bedTime, LocalDateTime wakeTime) {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.setBedTime(bedTime);
        request.setWakeTime(wakeTime);
        request.setMorningFeeling(MorningFeeling.GOOD);
        return request;
    }

    private CreateSleepLogRequest createRequestWithSleepDate(LocalDate sleepDate, LocalDateTime bedTime, LocalDateTime wakeTime) {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.setSleepDate(sleepDate);
        request.setBedTime(bedTime);
        request.setWakeTime(wakeTime);
        request.setMorningFeeling(MorningFeeling.GOOD);
        return request;
    }
}
