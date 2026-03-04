package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;
import com.noom.interview.fullstack.sleep.model.MorningFeeling;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SleepLogController.class)
class SleepLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SleepLogService sleepLogService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createSleepLog_shouldReturnCreatedSleepLog() throws Exception {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.bedTime = LocalDateTime.of(2026, 3, 2, 23, 0);
        request.wakeTime = LocalDateTime.of(2026, 3, 3, 7, 30);
        request.morningFeeling = MorningFeeling.GOOD;

        SleepLog savedLog = new SleepLog(1, LocalDate.of(2026, 3, 3),
                request.bedTime, request.wakeTime, MorningFeeling.GOOD);
        savedLog.setId(1);

        when(sleepLogService.createSleepLog(eq(1), any(), any(), any())).thenReturn(savedLog);

        mockMvc.perform(post("/api/sleep")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.morningFeeling").value("GOOD"))
                .andExpect(jsonPath("$.totalTimeInBedMinutes").value(510));
    }

    @Test
    void createSleepLog_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.bedTime = LocalDateTime.of(2026, 3, 2, 23, 0);
        request.wakeTime = LocalDateTime.of(2026, 3, 3, 7, 30);
        request.morningFeeling = MorningFeeling.GOOD;

        mockMvc.perform(post("/api/sleep")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLastNightSleep_shouldReturnSleepLog_whenExists() throws Exception {
        SleepLog sleepLog = new SleepLog(1, LocalDate.of(2026, 3, 3),
                LocalDateTime.of(2026, 3, 2, 23, 0),
                LocalDateTime.of(2026, 3, 3, 7, 30),
                MorningFeeling.GOOD);
        sleepLog.setId(1);

        when(sleepLogService.getLastNightSleep(1)).thenReturn(Optional.of(sleepLog));

        mockMvc.perform(get("/api/sleep/last-night")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.morningFeeling").value("GOOD"))
                .andExpect(jsonPath("$.totalTimeInBedMinutes").value(510));
    }

    @Test
    void getLastNightSleep_shouldReturnNotFound_whenNotExists() throws Exception {
        when(sleepLogService.getLastNightSleep(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sleep/last-night")
                        .header("X-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLastNightSleep_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/api/sleep/last-night"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSleepLog_shouldReturnBadRequest_whenWakeTimeBeforeBedTime() throws Exception {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.bedTime = LocalDateTime.of(2026, 3, 3, 7, 30);
        request.wakeTime = LocalDateTime.of(2026, 3, 2, 23, 0);
        request.morningFeeling = MorningFeeling.GOOD;

        mockMvc.perform(post("/api/sleep")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSleepLog_shouldReturnBadRequest_whenWakeTimeEqualsBedTime() throws Exception {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.bedTime = LocalDateTime.of(2026, 3, 3, 7, 30);
        request.wakeTime = LocalDateTime.of(2026, 3, 3, 7, 30);
        request.morningFeeling = MorningFeeling.GOOD;

        mockMvc.perform(post("/api/sleep")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSleepLog_shouldReturnBadRequest_whenBedTimeMissing() throws Exception {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.wakeTime = LocalDateTime.of(2026, 3, 3, 7, 30);
        request.morningFeeling = MorningFeeling.GOOD;

        mockMvc.perform(post("/api/sleep")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSleepLog_shouldReturnBadRequest_whenMorningFeelingMissing() throws Exception {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.bedTime = LocalDateTime.of(2026, 3, 2, 23, 0);
        request.wakeTime = LocalDateTime.of(2026, 3, 3, 7, 30);

        mockMvc.perform(post("/api/sleep")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
