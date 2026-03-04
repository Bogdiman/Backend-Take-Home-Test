package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.noom.interview.fullstack.sleep.dto.CreateSleepLogRequest;
import com.noom.interview.fullstack.sleep.dto.SleepAveragesResponse;
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
import java.time.LocalTime;
import java.util.Map;
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
        request.setBedTime(LocalDateTime.of(2026, 3, 2, 23, 0));
        request.setWakeTime(LocalDateTime.of(2026, 3, 3, 7, 30));
        request.setMorningFeeling(MorningFeeling.GOOD);

        SleepLog savedLog = new SleepLog(1, LocalDate.of(2026, 3, 3),
                request.getBedTime(), request.getWakeTime(), MorningFeeling.GOOD);
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
        request.setBedTime(LocalDateTime.of(2026, 3, 2, 23, 0));
        request.setWakeTime(LocalDateTime.of(2026, 3, 3, 7, 30));
        request.setMorningFeeling(MorningFeeling.GOOD);

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
        request.setBedTime(LocalDateTime.of(2026, 3, 3, 7, 30));
        request.setWakeTime(LocalDateTime.of(2026, 3, 2, 23, 0));
        request.setMorningFeeling(MorningFeeling.GOOD);

        mockMvc.perform(post("/api/sleep")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSleepLog_shouldReturnBadRequest_whenWakeTimeEqualsBedTime() throws Exception {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.setBedTime(LocalDateTime.of(2026, 3, 3, 7, 30));
        request.setWakeTime(LocalDateTime.of(2026, 3, 3, 7, 30));
        request.setMorningFeeling(MorningFeeling.GOOD);

        mockMvc.perform(post("/api/sleep")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSleepLog_shouldReturnBadRequest_whenBedTimeMissing() throws Exception {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.setWakeTime(LocalDateTime.of(2026, 3, 3, 7, 30));
        request.setMorningFeeling(MorningFeeling.GOOD);

        mockMvc.perform(post("/api/sleep")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSleepLog_shouldReturnBadRequest_whenMorningFeelingMissing() throws Exception {
        CreateSleepLogRequest request = new CreateSleepLogRequest();
        request.setBedTime(LocalDateTime.of(2026, 3, 2, 23, 0));
        request.setWakeTime(LocalDateTime.of(2026, 3, 3, 7, 30));

        mockMvc.perform(post("/api/sleep")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLast30DayAverages_shouldReturnAverages_whenDataExists() throws Exception {
        SleepAveragesResponse response = new SleepAveragesResponse(
                LocalDate.of(2026, 2, 2),
                LocalDate.of(2026, 3, 3),
                10,
                480,
                LocalTime.of(23, 0),
                LocalTime.of(7, 0),
                Map.of(MorningFeeling.GOOD, 5L, MorningFeeling.OK, 3L, MorningFeeling.BAD, 2L)
        );

        when(sleepLogService.getLast30DayAverages(1)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/sleep/averages")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2026-02-02"))
                .andExpect(jsonPath("$.endDate").value("2026-03-03"))
                .andExpect(jsonPath("$.totalNights").value(10))
                .andExpect(jsonPath("$.averageTotalTimeInBedMinutes").value(480))
                .andExpect(jsonPath("$.averageBedTime").value("23:00:00"))
                .andExpect(jsonPath("$.averageWakeTime").value("07:00:00"))
                .andExpect(jsonPath("$.morningFeelingFrequencies.GOOD").value(5))
                .andExpect(jsonPath("$.morningFeelingFrequencies.OK").value(3))
                .andExpect(jsonPath("$.morningFeelingFrequencies.BAD").value(2));
    }

    @Test
    void getLast30DayAverages_shouldReturnNotFound_whenNoData() throws Exception {
        when(sleepLogService.getLast30DayAverages(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sleep/averages")
                        .header("X-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLast30DayAverages_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/api/sleep/averages"))
                .andExpect(status().isBadRequest());
    }
}
