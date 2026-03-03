package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sleep")
public class SleepLogController {

    private final SleepLogService service;

    public SleepLogController(SleepLogService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SleepLog createSleepLog(
            @RequestHeader("X-User-Id") Integer userId,
            @RequestBody CreateSleepLogRequest request) {
        return service.createSleepLog(userId, request.bedTime, request.wakeTime, request.morningFeeling);
    }
}
