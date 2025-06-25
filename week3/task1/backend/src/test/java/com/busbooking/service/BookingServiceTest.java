package com.busbooking.service;

import com.busbooking.entity.Booking;
import com.busbooking.entity.Bus;
import com.busbooking.entity.Schedule;
import com.busbooking.entity.User;
import com.busbooking.repository.BookingRepository;
import com.busbooking.repository.BusRepository;
import com.busbooking.repository.ScheduleRepository;
import com.busbooking.repository.UserRepository;
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
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private User user;
    private Bus bus;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        // Setup user
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");

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
        schedule.setDepartureTime(LocalDateTime.now().plusHours(1));
        schedule.setArrivalTime(LocalDateTime.now().plusHours(3));
        schedule.setAvailableSeats(40);
        schedule.setFare(100.0);

        // Setup booking
        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setNumberOfSeats(1);
        booking.setTotalAmount(100.0);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
    }

    @Test
    void createBooking_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        Booking response = bookingService.createBooking(1L, 1L, 1);

        // Assert
        assertNotNull(response);
        assertEquals(booking.getId(), response.getId());
        assertEquals(booking.getUser().getId(), response.getUser().getId());
        assertEquals(booking.getSchedule().getId(), response.getSchedule().getId());
        assertEquals(booking.getNumberOfSeats(), response.getNumberOfSeats());
        assertEquals(booking.getTotalAmount(), response.getTotalAmount());
        assertEquals(booking.getStatus(), response.getStatus());

        verify(userRepository).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void createBooking_UserNotFound() {
        // Arrange
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(1L, 1L, 1));
        verify(scheduleRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_ScheduleNotFound() {
        // Arrange
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(1L, 1L, 1));
        verify(scheduleRepository).findById(1L);
        verify(userRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_InsufficientSeats() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        schedule.setAvailableSeats(0);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(1L, 1L, 1));
        verify(userRepository).findById(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById_Success() {
        // Arrange
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // Act
        Booking response = bookingService.getBookingById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(booking.getId(), response.getId());
        assertEquals(booking.getUser().getId(), response.getUser().getId());
        assertEquals(booking.getSchedule().getId(), response.getSchedule().getId());
        assertEquals(booking.getNumberOfSeats(), response.getNumberOfSeats());
        assertEquals(booking.getTotalAmount(), response.getTotalAmount());
        assertEquals(booking.getStatus(), response.getStatus());

        verify(bookingRepository).findById(1L);
    }

    @Test
    void getBookingById_NotFound() {
        // Arrange
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.getBookingById(1L));
        verify(bookingRepository).findById(1L);
    }

    @Test
    void getUserBookings_Success() {
        // Arrange
        when(bookingRepository.findByUserId(anyLong())).thenReturn(Arrays.asList(booking));

        // Act
        List<Booking> responses = bookingService.getUserBookings(1L);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(booking.getId(), responses.get(0).getId());
        assertEquals(booking.getUser().getId(), responses.get(0).getUser().getId());
        assertEquals(booking.getSchedule().getId(), responses.get(0).getSchedule().getId());
        assertEquals(booking.getNumberOfSeats(), responses.get(0).getNumberOfSeats());
        assertEquals(booking.getTotalAmount(), responses.get(0).getTotalAmount());
        assertEquals(booking.getStatus(), responses.get(0).getStatus());

        verify(bookingRepository).findByUserId(1L);
    }

    @Test
    void cancelBooking_Success() {
        // Arrange
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(any(Booking.class));
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void cancelBooking_NotFound() {
        // Arrange
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.cancelBooking(1L));
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_AlreadyCancelled() {
        // Arrange
        booking.setStatus("CANCELLED");
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.cancelBooking(1L));
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }
} 