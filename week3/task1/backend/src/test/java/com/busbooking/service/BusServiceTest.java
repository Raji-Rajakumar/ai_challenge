package com.busbooking.service;

import com.busbooking.entity.Bus;
import com.busbooking.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private BusService busService;

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
    }

    @Test
    void createBus_Success() {
        // Arrange
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        // Act
        Bus response = busService.createBus(bus);

        // Assert
        assertNotNull(response);
        assertEquals(bus.getId(), response.getId());
        assertEquals(bus.getBusNumber(), response.getBusNumber());
        assertEquals(bus.getBusName(), response.getBusName());
        assertEquals(bus.getTotalSeats(), response.getTotalSeats());
        assertEquals(bus.getBusType(), response.getBusType());

        verify(busRepository).save(any(Bus.class));
    }

    @Test
    void getBusById_Success() {
        // Arrange
        when(busRepository.findById(anyLong())).thenReturn(Optional.of(bus));

        // Act
        Bus response = busService.getBusById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(bus.getId(), response.getId());
        assertEquals(bus.getBusNumber(), response.getBusNumber());
        assertEquals(bus.getBusName(), response.getBusName());
        assertEquals(bus.getTotalSeats(), response.getTotalSeats());
        assertEquals(bus.getBusType(), response.getBusType());

        verify(busRepository).findById(1L);
    }

    @Test
    void getBusById_NotFound() {
        // Arrange
        when(busRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> busService.getBusById(1L));
        verify(busRepository).findById(1L);
    }

    @Test
    void getAllBuses_Success() {
        // Arrange
        when(busRepository.findAll()).thenReturn(Arrays.asList(bus));

        // Act
        List<Bus> responses = busService.getAllBuses();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(bus.getId(), responses.get(0).getId());
        assertEquals(bus.getBusNumber(), responses.get(0).getBusNumber());
        assertEquals(bus.getBusName(), responses.get(0).getBusName());
        assertEquals(bus.getTotalSeats(), responses.get(0).getTotalSeats());
        assertEquals(bus.getBusType(), responses.get(0).getBusType());

        verify(busRepository).findAll();
    }

    @Test
    void updateBus_Success() {
        // Arrange
        Bus updatedBus = new Bus();
        updatedBus.setBusNumber("BUS002");
        updatedBus.setBusName("Updated Bus");
        updatedBus.setTotalSeats(50);
        updatedBus.setBusType("Non-AC");

        when(busRepository.findById(anyLong())).thenReturn(Optional.of(bus));
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        // Act
        Bus response = busService.updateBus(1L, updatedBus);

        // Assert
        assertNotNull(response);
        assertEquals(bus.getId(), response.getId());
        assertEquals(updatedBus.getBusNumber(), response.getBusNumber());
        assertEquals(updatedBus.getBusName(), response.getBusName());
        assertEquals(updatedBus.getTotalSeats(), response.getTotalSeats());
        assertEquals(updatedBus.getBusType(), response.getBusType());

        verify(busRepository).findById(1L);
        verify(busRepository).save(any(Bus.class));
    }

    @Test
    void updateBus_NotFound() {
        // Arrange
        when(busRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> busService.updateBus(1L, bus));
        verify(busRepository).findById(1L);
        verify(busRepository, never()).save(any(Bus.class));
    }

    @Test
    void deleteBus_Success() {
        // Arrange
        when(busRepository.findById(anyLong())).thenReturn(Optional.of(bus));
        doNothing().when(busRepository).delete(any(Bus.class));

        // Act
        busService.deleteBus(1L);

        // Assert
        verify(busRepository).findById(1L);
        verify(busRepository).delete(bus);
    }

    @Test
    void deleteBus_NotFound() {
        // Arrange
        when(busRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> busService.deleteBus(1L));
        verify(busRepository).findById(1L);
        verify(busRepository, never()).delete(any(Bus.class));
    }
} 