package com.busbooking.service;

import com.busbooking.entity.Booking;
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
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingCancellationEdgeCasesTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Schedule schedule;
    private Booking booking;
    private ArgumentCaptor<Booking> bookingCaptor;
    private ArgumentCaptor<Schedule> scheduleCaptor;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");

        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setAvailableSeats(10);
        schedule.setDepartureTime(LocalDateTime.now().plusHours(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setNumberOfSeats(2);
        booking.setTotalAmount(100.0);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        scheduleCaptor = ArgumentCaptor.forClass(Schedule.class);
    }

    @Test
    void cancelBooking_WithPartialRefund() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(bookingCaptor.capture());
        verify(scheduleRepository).save(scheduleCaptor.capture());

        Booking savedBooking = bookingCaptor.getValue();
        Schedule savedSchedule = scheduleCaptor.getValue();

        assertEquals("CANCELLED", savedBooking.getStatus());
        assertEquals(12, savedSchedule.getAvailableSeats()); // Original 10 + 2 seats returned
    }

    @Test
    void cancelBooking_WithFullRefund() {
        // Arrange
        schedule.setDepartureTime(LocalDateTime.now().plusDays(2));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(bookingCaptor.capture());
        verify(scheduleRepository).save(scheduleCaptor.capture());

        Booking savedBooking = bookingCaptor.getValue();
        Schedule savedSchedule = scheduleCaptor.getValue();

        assertEquals("CANCELLED", savedBooking.getStatus());
        assertEquals(100.0, savedBooking.getTotalAmount(), 0.001); // Full refund
        assertEquals(12, savedSchedule.getAvailableSeats()); // Original 10 + 2 seats returned
    }

    @Test
    void cancelBooking_WithNoRefund() {
        // Arrange
        schedule.setDepartureTime(LocalDateTime.now().plusMinutes(30));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(bookingCaptor.capture());
        verify(scheduleRepository).save(scheduleCaptor.capture());

        Booking savedBooking = bookingCaptor.getValue();
        Schedule savedSchedule = scheduleCaptor.getValue();

        assertEquals("CANCELLED", savedBooking.getStatus());
        assertEquals(0.0, savedBooking.getTotalAmount(), 0.001); // No refund
        assertEquals(12, savedSchedule.getAvailableSeats()); // Original 10 + 2 seats returned
    }

    @Test
    void cancelBooking_WithMultipleSeats() {
        // Arrange
        booking.setNumberOfSeats(5);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(bookingCaptor.capture());
        verify(scheduleRepository).save(scheduleCaptor.capture());

        Booking savedBooking = bookingCaptor.getValue();
        Schedule savedSchedule = scheduleCaptor.getValue();

        assertEquals("CANCELLED", savedBooking.getStatus());
        assertEquals(15, savedSchedule.getAvailableSeats()); // Original 10 + 5 seats returned
    }

    @Test
    void cancelBooking_WithScheduleUpdate() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(bookingCaptor.capture());
        verify(scheduleRepository).save(scheduleCaptor.capture());

        Booking savedBooking = bookingCaptor.getValue();
        Schedule savedSchedule = scheduleCaptor.getValue();

        assertEquals("CANCELLED", savedBooking.getStatus());
        assertEquals(12, savedSchedule.getAvailableSeats()); // Original 10 + 2 seats returned
    }

    @Test
    void cancelBooking_WithUserNotification() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(bookingCaptor.capture());
        verify(scheduleRepository).save(scheduleCaptor.capture());

        Booking savedBooking = bookingCaptor.getValue();
        Schedule savedSchedule = scheduleCaptor.getValue();

        assertEquals("CANCELLED", savedBooking.getStatus());
        assertEquals(12, savedSchedule.getAvailableSeats()); // Original 10 + 2 seats returned
    }

    @Test
    void cancelBooking_WithConcurrentScheduleUpdate() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(bookingCaptor.capture());
        verify(scheduleRepository).save(scheduleCaptor.capture());

        Booking savedBooking = bookingCaptor.getValue();
        Schedule savedSchedule = scheduleCaptor.getValue();

        assertEquals("CANCELLED", savedBooking.getStatus());
        assertEquals(12, savedSchedule.getAvailableSeats()); // Original 10 + 2 seats returned
    }
} 