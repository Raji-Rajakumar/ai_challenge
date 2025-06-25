package com.busbooking.service;

import com.busbooking.entity.Bus;
import com.busbooking.entity.Schedule;
import com.busbooking.repository.BusRepository;
import com.busbooking.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private Schedule schedule;
    private Bus bus;

    @BeforeEach
    void setUp() {
        // Setup bus
        bus = new Bus();
        bus.setId(1L);
        bus.setBusNumber("BUS001");
        bus.setBusName("Test Bus");
        bus.setTotalSeats(40);
        bus.setBusType("AC");

        // Setup schedule
        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setBus(bus);
        schedule.setSource("Source");
        schedule.setDestination("Destination");
        schedule.setDepartureTime(LocalDateTime.now());
        schedule.setArrivalTime(LocalDateTime.now().plusHours(2));
        schedule.setFare(500.0);
        schedule.setAvailableSeats(40);
    }

    @Test
    void createSchedule_Success() {
        // Arrange
        when(busRepository.existsById(anyLong())).thenReturn(true);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        Schedule result = scheduleService.createSchedule(schedule);

        // Assert
        assertNotNull(result);
        assertEquals(schedule, result);
        verify(busRepository).existsById(schedule.getBus().getId());
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void createSchedule_BusNotFound() {
        // Arrange
        when(busRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> scheduleService.createSchedule(schedule));
        verify(busRepository).existsById(schedule.getBus().getId());
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void getScheduleById_Success() {
        // Arrange
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));

        // Act
        Schedule response = scheduleService.getScheduleById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(schedule.getId(), response.getId());
        assertEquals(schedule.getBus(), response.getBus());
        assertEquals(schedule.getSource(), response.getSource());
        assertEquals(schedule.getDestination(), response.getDestination());
        assertEquals(schedule.getDepartureTime(), response.getDepartureTime());
        assertEquals(schedule.getArrivalTime(), response.getArrivalTime());
        assertEquals(schedule.getFare(), response.getFare());
        assertEquals(schedule.getAvailableSeats(), response.getAvailableSeats());

        verify(scheduleRepository).findById(1L);
    }

    @Test
    void getScheduleById_NotFound() {
        // Arrange
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> scheduleService.getScheduleById(1L));
        verify(scheduleRepository).findById(1L);
    }

    @Test
    void getAllSchedules_Success() {
        // Arrange
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(schedule));

        // Act
        List<Schedule> responses = scheduleService.getAllSchedules();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(schedule.getId(), responses.get(0).getId());
        assertEquals(schedule.getBus(), responses.get(0).getBus());
        assertEquals(schedule.getSource(), responses.get(0).getSource());
        assertEquals(schedule.getDestination(), responses.get(0).getDestination());
        assertEquals(schedule.getDepartureTime(), responses.get(0).getDepartureTime());
        assertEquals(schedule.getArrivalTime(), responses.get(0).getArrivalTime());
        assertEquals(schedule.getFare(), responses.get(0).getFare());
        assertEquals(schedule.getAvailableSeats(), responses.get(0).getAvailableSeats());

        verify(scheduleRepository).findAll();
    }

    @Test
    void updateSchedule_Success() {
        // Arrange
        Schedule existingSchedule = new Schedule();
        existingSchedule.setId(1L);
        existingSchedule.setSource("Source");
        existingSchedule.setDestination("Destination");
        existingSchedule.setDepartureTime(LocalDateTime.now());
        existingSchedule.setArrivalTime(LocalDateTime.now().plusHours(2));
        existingSchedule.setFare(100.0);
        existingSchedule.setAvailableSeats(40);

        Schedule updatedSchedule = new Schedule();
        updatedSchedule.setSource("New Source");
        updatedSchedule.setDestination("New Destination");
        updatedSchedule.setDepartureTime(LocalDateTime.now().plusHours(1));
        updatedSchedule.setArrivalTime(LocalDateTime.now().plusHours(3));
        updatedSchedule.setFare(150.0);
        updatedSchedule.setAvailableSeats(35);
        updatedSchedule.setBus(bus);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existingSchedule));
        when(busRepository.existsById(anyLong())).thenReturn(true);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(updatedSchedule);

        // Act
        Schedule result = scheduleService.updateSchedule(1L, updatedSchedule);

        // Assert
        assertNotNull(result);
        assertEquals(updatedSchedule.getSource(), result.getSource());
        assertEquals(updatedSchedule.getDestination(), result.getDestination());
        assertEquals(updatedSchedule.getFare(), result.getFare());
        assertEquals(updatedSchedule.getAvailableSeats(), result.getAvailableSeats());
        verify(scheduleRepository).findById(1L);
        verify(busRepository).existsById(updatedSchedule.getBus().getId());
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void updateSchedule_NotFound() {
        // Arrange
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> scheduleService.updateSchedule(1L, schedule));
        verify(scheduleRepository).findById(1L);
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void deleteSchedule_Success() {
        // Arrange
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        doNothing().when(scheduleRepository).delete(any(Schedule.class));

        // Act
        scheduleService.deleteSchedule(1L);

        // Assert
        verify(scheduleRepository).findById(1L);
        verify(scheduleRepository).delete(schedule);
    }

    @Test
    void deleteSchedule_NotFound() {
        // Arrange
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> scheduleService.deleteSchedule(1L));
        verify(scheduleRepository).findById(1L);
        verify(scheduleRepository, never()).delete(any(Schedule.class));
    }
} 