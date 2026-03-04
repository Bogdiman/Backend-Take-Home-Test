package com.noom.interview.fullstack.sleep.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SleepIntervalValidator.class)
public @interface ValidSleepInterval {
    String message() default "Invalid sleep interval";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    int minMinutes() default 30;
    int maxMinutes() default 1440; // 24 hours
}
