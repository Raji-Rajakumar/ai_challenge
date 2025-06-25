package com.busbooking.controller;

import com.busbooking.entity.Booking;
import com.busbooking.service.BookingService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private Booking booking;
    private List<Booking> bookings;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(1L);
        booking.setNumberOfSeats(2);
        booking.setTotalAmount(100.0);
        booking.setStatus("CONFIRMED");

        bookings = Arrays.asList(booking);
    }

    @Test
    void createBooking_Success() {
        // Arrange
        when(bookingService.createBooking(anyLong(), anyLong(), anyInt())).thenReturn(booking);

        // Act
        ResponseEntity<Booking> response = bookingController.createBooking(1L, 1L, 2);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(booking.getId(), response.getBody().getId());
        assertEquals(booking.getNumberOfSeats(), response.getBody().getNumberOfSeats());
        assertEquals(booking.getTotalAmount(), response.getBody().getTotalAmount());
        assertEquals(booking.getStatus(), response.getBody().getStatus());

        verify(bookingService).createBooking(1L, 1L, 2);
    }

    @Test
    void createBooking_NotEnoughSeats() {
        // Arrange
        when(bookingService.createBooking(anyLong(), anyLong(), anyInt()))
            .thenThrow(new RuntimeException("Not enough seats available"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingController.createBooking(1L, 1L, 2));
        verify(bookingService).createBooking(1L, 1L, 2);
    }

    @Test
    void getBooking_Success() {
        // Arrange
        when(bookingService.getBookingById(anyLong())).thenReturn(booking);

        // Act
        ResponseEntity<Booking> response = bookingController.getBookingById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(booking.getId(), response.getBody().getId());
        assertEquals(booking.getNumberOfSeats(), response.getBody().getNumberOfSeats());
        assertEquals(booking.getTotalAmount(), response.getBody().getTotalAmount());
        assertEquals(booking.getStatus(), response.getBody().getStatus());

        verify(bookingService).getBookingById(1L);
    }

    @Test
    void getBooking_NotFound() {
        // Arrange
        when(bookingService.getBookingById(anyLong()))
            .thenThrow(new RuntimeException("Booking not found"));

        // Act
        ResponseEntity<Booking> response = bookingController.getBookingById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(bookingService).getBookingById(1L);
    }

    @Test
    void getUserBookings_Success() {
        // Arrange
        when(bookingService.getUserBookings(anyLong())).thenReturn(bookings);

        // Act
        ResponseEntity<List<Booking>> response = bookingController.getUserBookings(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(booking.getId(), response.getBody().get(0).getId());
        assertEquals(booking.getNumberOfSeats(), response.getBody().get(0).getNumberOfSeats());
        assertEquals(booking.getTotalAmount(), response.getBody().get(0).getTotalAmount());
        assertEquals(booking.getStatus(), response.getBody().get(0).getStatus());

        verify(bookingService).getUserBookings(1L);
    }

    @Test
    void cancelBooking_Success() {
        // Arrange
        doNothing().when(bookingService).cancelBooking(anyLong());

        // Act
        ResponseEntity<Void> response = bookingController.cancelBooking(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(bookingService).cancelBooking(1L);
    }

    @Test
    void cancelBooking_AlreadyCancelled() {
        // Arrange
        doThrow(new RuntimeException("Booking is already cancelled"))
            .when(bookingService).cancelBooking(anyLong());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingController.cancelBooking(1L));
        verify(bookingService).cancelBooking(1L);
    }
} 