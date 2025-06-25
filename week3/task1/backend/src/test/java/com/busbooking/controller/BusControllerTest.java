package com.busbooking.controller;

import com.busbooking.entity.Bus;
import com.busbooking.service.BusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusControllerTest {

    @Mock
    private BusService busService;

    @InjectMocks
    private BusController busController;

    private Bus bus;
    private List<Bus> buses;

    @BeforeEach
    void setUp() {
        bus = new Bus();
        bus.setId(1L);
        bus.setBusNumber("BUS001");
        bus.setBusName("Test Bus");
        bus.setTotalSeats(40);
        bus.setBusType("AC");

        buses = Arrays.asList(bus);
    }

    @Test
    void createBus_Success() {
        // Arrange
        when(busService.createBus(any(Bus.class))).thenReturn(bus);

        // Act
        ResponseEntity<Bus> response = busController.createBus(bus);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(bus.getId(), response.getBody().getId());
        assertEquals(bus.getBusNumber(), response.getBody().getBusNumber());
        assertEquals(bus.getBusName(), response.getBody().getBusName());
        assertEquals(bus.getTotalSeats(), response.getBody().getTotalSeats());
        assertEquals(bus.getBusType(), response.getBody().getBusType());

        verify(busService).createBus(bus);
    }

    @Test
    void getBus_Success() {
        // Arrange
        when(busService.getBusById(anyLong())).thenReturn(bus);

        // Act
        ResponseEntity<Bus> response = busController.getBusById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(bus.getId(), response.getBody().getId());
        assertEquals(bus.getBusNumber(), response.getBody().getBusNumber());
        assertEquals(bus.getBusName(), response.getBody().getBusName());
        assertEquals(bus.getTotalSeats(), response.getBody().getTotalSeats());
        assertEquals(bus.getBusType(), response.getBody().getBusType());

        verify(busService).getBusById(1L);
    }

    @Test
    void getBus_NotFound() {
        // Arrange
        when(busService.getBusById(anyLong()))
            .thenThrow(new RuntimeException("Bus not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> busController.getBusById(1L));
        verify(busService).getBusById(1L);
    }

    @Test
    void getAllBuses_Success() {
        // Arrange
        when(busService.getAllBuses()).thenReturn(buses);

        // Act
        ResponseEntity<List<Bus>> response = busController.getAllBuses();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(bus.getId(), response.getBody().get(0).getId());
        assertEquals(bus.getBusNumber(), response.getBody().get(0).getBusNumber());
        assertEquals(bus.getBusName(), response.getBody().get(0).getBusName());
        assertEquals(bus.getTotalSeats(), response.getBody().get(0).getTotalSeats());
        assertEquals(bus.getBusType(), response.getBody().get(0).getBusType());

        verify(busService).getAllBuses();
    }

    @Test
    void updateBus_Success() {
        // Arrange
        Bus updatedBus = new Bus();
        updatedBus.setId(1L);
        updatedBus.setBusNumber("BUS002");
        updatedBus.setBusName("Updated Bus");
        updatedBus.setTotalSeats(50);
        updatedBus.setBusType("Non-AC");

        when(busService.updateBus(anyLong(), any(Bus.class))).thenReturn(updatedBus);

        // Act
        ResponseEntity<Bus> response = busController.updateBus(1L, updatedBus);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(updatedBus.getId(), response.getBody().getId());
        assertEquals(updatedBus.getBusNumber(), response.getBody().getBusNumber());
        assertEquals(updatedBus.getBusName(), response.getBody().getBusName());
        assertEquals(updatedBus.getTotalSeats(), response.getBody().getTotalSeats());
        assertEquals(updatedBus.getBusType(), response.getBody().getBusType());

        verify(busService).updateBus(1L, updatedBus);
    }

    @Test
    void updateBus_NotFound() {
        // Arrange
        when(busService.updateBus(anyLong(), any(Bus.class)))
            .thenThrow(new RuntimeException("Bus not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> busController.updateBus(1L, bus));
        verify(busService).updateBus(1L, bus);
    }

    @Test
    void deleteBus_Success() {
        // Arrange
        doNothing().when(busService).deleteBus(anyLong());

        // Act
        ResponseEntity<Void> response = busController.deleteBus(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(busService).deleteBus(1L);
    }

    @Test
    void deleteBus_NotFound() {
        // Arrange
        doThrow(new RuntimeException("Bus not found"))
            .when(busService).deleteBus(anyLong());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> busController.deleteBus(1L));
        verify(busService).deleteBus(1L);
    }
} 