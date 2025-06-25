package com.busbooking.service;

import com.busbooking.entity.Booking;
import com.busbooking.entity.Bus;
import com.busbooking.entity.Schedule;
import com.busbooking.entity.User;
import com.busbooking.repository.BookingRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBookingEdgeCasesTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Bus bus;
    private Schedule schedule;
    private Booking booking;

    @BeforeEach
    void setUp() {
        // Setup user
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPhoneNumber("1234567890");

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
        schedule.setDepartureTime(LocalDateTime.now().plusHours(2));
        schedule.setArrivalTime(LocalDateTime.now().plusHours(4));
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
    void createBooking_UserWithMultipleBookings() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByUserId(anyLong())).thenReturn(
            Arrays.asList(booking, booking, booking, booking, booking)
        );

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            bookingService.createBooking(1L, 1L, 1));
    }

    @Test
    void createBooking_UserWithCancelledBookings() {
        // Arrange
        Booking cancelledBooking = new Booking();
        cancelledBooking.setStatus("CANCELLED");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByUserId(anyLong())).thenReturn(
            Arrays.asList(cancelledBooking, cancelledBooking)
        );
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        Booking result = bookingService.createBooking(1L, 1L, 1);

        // Assert
        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void createBooking_UserWithExpiredBookings() {
        // Arrange
        Booking expiredBooking = new Booking();
        expiredBooking.setStatus("EXPIRED");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByUserId(anyLong())).thenReturn(
            Arrays.asList(expiredBooking, expiredBooking)
        );
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        Booking result = bookingService.createBooking(1L, 1L, 1);

        // Assert
        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void createBooking_UserWithOverlappingBookings() {
        // Arrange
        Schedule overlappingSchedule = new Schedule();
        overlappingSchedule.setId(2L);
        overlappingSchedule.setBus(bus);
        overlappingSchedule.setDepartureTime(schedule.getDepartureTime().plusMinutes(30));
        overlappingSchedule.setArrivalTime(schedule.getArrivalTime().plusMinutes(30));
        
        Booking overlappingBooking = new Booking();
        overlappingBooking.setSchedule(overlappingSchedule);
        overlappingBooking.setStatus("CONFIRMED");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByUserId(anyLong())).thenReturn(
            Arrays.asList(overlappingBooking)
        );

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            bookingService.createBooking(1L, 1L, 1));
    }

    @Test
    void createBooking_UserWithPastBookings() {
        // Arrange
        Schedule pastSchedule = new Schedule();
        pastSchedule.setId(2L);
        pastSchedule.setBus(bus);
        pastSchedule.setDepartureTime(LocalDateTime.now().minusDays(1));
        pastSchedule.setArrivalTime(LocalDateTime.now().minusDays(1).plusHours(2));
        
        Booking pastBooking = new Booking();
        pastBooking.setSchedule(pastSchedule);
        pastBooking.setStatus("COMPLETED");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByUserId(anyLong())).thenReturn(
            Arrays.asList(pastBooking)
        );
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        Booking result = bookingService.createBooking(1L, 1L, 1);

        // Assert
        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void createBooking_UserWithFutureBookings() {
        // Arrange
        Schedule futureSchedule = new Schedule();
        futureSchedule.setId(2L);
        futureSchedule.setBus(bus);
        futureSchedule.setDepartureTime(LocalDateTime.now().plusDays(2));
        futureSchedule.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(2));
        
        Booking futureBooking = new Booking();
        futureBooking.setSchedule(futureSchedule);
        futureBooking.setStatus("CONFIRMED");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByUserId(anyLong())).thenReturn(
            Arrays.asList(futureBooking)
        );
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        Booking result = bookingService.createBooking(1L, 1L, 1);

        // Assert
        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
    }
} 