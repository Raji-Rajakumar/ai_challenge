package com.busbooking.controller;

import com.busbooking.entity.Schedule;
import com.busbooking.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    private Schedule schedule;
    private List<Schedule> schedules;

    @BeforeEach
    void setUp() {
        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setSource("City A");
        schedule.setDestination("City B");
        schedule.setDepartureTime(LocalDateTime.now());
        schedule.setArrivalTime(LocalDateTime.now().plusHours(2));
        schedule.setFare(100.0);
        schedule.setAvailableSeats(40);

        schedules = Arrays.asList(schedule);
    }

    @Test
    void createSchedule_Success() {
        // Arrange
        when(scheduleService.createSchedule(any(Schedule.class))).thenReturn(schedule);

        // Act
        ResponseEntity<Schedule> response = scheduleController.createSchedule(schedule);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(schedule.getId(), response.getBody().getId());
        assertEquals(schedule.getSource(), response.getBody().getSource());
        assertEquals(schedule.getDestination(), response.getBody().getDestination());
        assertEquals(schedule.getDepartureTime(), response.getBody().getDepartureTime());
        assertEquals(schedule.getArrivalTime(), response.getBody().getArrivalTime());
        assertEquals(schedule.getFare(), response.getBody().getFare());
        assertEquals(schedule.getAvailableSeats(), response.getBody().getAvailableSeats());

        verify(scheduleService).createSchedule(schedule);
    }

    @Test
    void getSchedule_Success() {
        // Arrange
        when(scheduleService.getScheduleById(anyLong())).thenReturn(schedule);

        // Act
        ResponseEntity<Schedule> response = scheduleController.getScheduleById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(schedule.getId(), response.getBody().getId());
        assertEquals(schedule.getSource(), response.getBody().getSource());
        assertEquals(schedule.getDestination(), response.getBody().getDestination());
        assertEquals(schedule.getDepartureTime(), response.getBody().getDepartureTime());
        assertEquals(schedule.getArrivalTime(), response.getBody().getArrivalTime());
        assertEquals(schedule.getFare(), response.getBody().getFare());
        assertEquals(schedule.getAvailableSeats(), response.getBody().getAvailableSeats());

        verify(scheduleService).getScheduleById(1L);
    }

    @Test
    void getSchedule_NotFound() {
        // Arrange
        when(scheduleService.getScheduleById(anyLong()))
            .thenThrow(new RuntimeException("Schedule not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> scheduleController.getScheduleById(1L));
        verify(scheduleService).getScheduleById(1L);
    }

    @Test
    void getAllSchedules_Success() {
        // Arrange
        when(scheduleService.getAllSchedules()).thenReturn(schedules);

        // Act
        ResponseEntity<List<Schedule>> response = scheduleController.getAllSchedules();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(schedule.getId(), response.getBody().get(0).getId());
        assertEquals(schedule.getSource(), response.getBody().get(0).getSource());
        assertEquals(schedule.getDestination(), response.getBody().get(0).getDestination());
        assertEquals(schedule.getDepartureTime(), response.getBody().get(0).getDepartureTime());
        assertEquals(schedule.getArrivalTime(), response.getBody().get(0).getArrivalTime());
        assertEquals(schedule.getFare(), response.getBody().get(0).getFare());
        assertEquals(schedule.getAvailableSeats(), response.getBody().get(0).getAvailableSeats());

        verify(scheduleService).getAllSchedules();
    }

    @Test
    void searchSchedules_Success() {
        // Arrange
        when(scheduleService.searchSchedules(anyString(), anyString())).thenReturn(schedules);

        // Act
        ResponseEntity<List<Schedule>> response = scheduleController.searchSchedules("City A", "City B");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(schedule.getId(), response.getBody().get(0).getId());
        assertEquals(schedule.getSource(), response.getBody().get(0).getSource());
        assertEquals(schedule.getDestination(), response.getBody().get(0).getDestination());
        assertEquals(schedule.getDepartureTime(), response.getBody().get(0).getDepartureTime());
        assertEquals(schedule.getArrivalTime(), response.getBody().get(0).getArrivalTime());
        assertEquals(schedule.getFare(), response.getBody().get(0).getFare());
        assertEquals(schedule.getAvailableSeats(), response.getBody().get(0).getAvailableSeats());

        verify(scheduleService).searchSchedules("City A", "City B");
    }

    @Test
    void updateSchedule_Success() {
        // Arrange
        Schedule updatedSchedule = new Schedule();
        updatedSchedule.setId(1L);
        updatedSchedule.setSource("City C");
        updatedSchedule.setDestination("City D");
        updatedSchedule.setDepartureTime(LocalDateTime.now().plusHours(1));
        updatedSchedule.setArrivalTime(LocalDateTime.now().plusHours(3));
        updatedSchedule.setFare(150.0);
        updatedSchedule.setAvailableSeats(30);

        when(scheduleService.updateSchedule(anyLong(), any(Schedule.class))).thenReturn(updatedSchedule);

        // Act
        ResponseEntity<Schedule> response = scheduleController.updateSchedule(1L, updatedSchedule);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(updatedSchedule.getId(), response.getBody().getId());
        assertEquals(updatedSchedule.getSource(), response.getBody().getSource());
        assertEquals(updatedSchedule.getDestination(), response.getBody().getDestination());
        assertEquals(updatedSchedule.getDepartureTime(), response.getBody().getDepartureTime());
        assertEquals(updatedSchedule.getArrivalTime(), response.getBody().getArrivalTime());
        assertEquals(updatedSchedule.getFare(), response.getBody().getFare());
        assertEquals(updatedSchedule.getAvailableSeats(), response.getBody().getAvailableSeats());

        verify(scheduleService).updateSchedule(1L, updatedSchedule);
    }

    @Test
    void updateSchedule_NotFound() {
        // Arrange
        when(scheduleService.updateSchedule(anyLong(), any(Schedule.class)))
            .thenThrow(new RuntimeException("Schedule not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> scheduleController.updateSchedule(1L, schedule));
        verify(scheduleService).updateSchedule(1L, schedule);
    }

    @Test
    void deleteSchedule_Success() {
        // Arrange
        doNothing().when(scheduleService).deleteSchedule(anyLong());

        // Act
        ResponseEntity<Void> response = scheduleController.deleteSchedule(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(scheduleService).deleteSchedule(1L);
    }

    @Test
    void deleteSchedule_NotFound() {
        // Arrange
        doThrow(new RuntimeException("Schedule not found"))
            .when(scheduleService).deleteSchedule(anyLong());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> scheduleController.deleteSchedule(1L));
        verify(scheduleService).deleteSchedule(1L);
    }
} 