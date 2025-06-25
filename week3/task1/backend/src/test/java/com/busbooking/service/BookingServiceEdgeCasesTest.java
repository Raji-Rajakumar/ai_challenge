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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceEdgeCasesTest {

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
    void createBooking_ConcurrentBookings_SameSchedule() throws InterruptedException {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByUserId(anyLong())).thenReturn(Arrays.asList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
            Schedule savedSchedule = invocation.getArgument(0);
            if (savedSchedule.getAvailableSeats() < 0) {
                throw new RuntimeException("Not enough seats available");
            }
            return savedSchedule;
        });

        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successfulBookings = new AtomicInteger(0);
        AtomicInteger failedBookings = new AtomicInteger(0);

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    bookingService.createBooking(1L, 1L, 1);
                    successfulBookings.incrementAndGet();
                } catch (RuntimeException e) {
                    if (e.getMessage().contains("currently being booked") || 
                        e.getMessage().contains("Not enough seats available")) {
                        failedBookings.incrementAndGet();
                    } else {
                        throw e;
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // Assert
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void createBooking_MaximumSeats() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(bookingRepository.findByUserId(anyLong())).thenReturn(Arrays.asList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        bookingService.createBooking(1L, 1L, 40);

        // Assert
        verify(scheduleRepository).save(argThat(s -> s.getAvailableSeats() == 0));
    }

    @Test
    void createBooking_ZeroSeats() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            bookingService.createBooking(1L, 1L, 0));
    }

    @Test
    void createBooking_NegativeSeats() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            bookingService.createBooking(1L, 1L, -1));
    }

    @Test
    void createBooking_ScheduleAboutToDepart() {
        // Arrange
        schedule.setDepartureTime(LocalDateTime.now().plusMinutes(30));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            bookingService.createBooking(1L, 1L, 1));
    }

    @Test
    void createBooking_PastSchedule() {
        // Arrange
        schedule.setDepartureTime(LocalDateTime.now().minusHours(1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            bookingService.createBooking(1L, 1L, 1));
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
    void cancelBooking_WithPartialRefund() {
        // Arrange
        schedule.setDepartureTime(LocalDateTime.now().plusHours(12));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(argThat(b -> 
            "CANCELLED".equals(b.getStatus()) && 
            b.getTotalAmount() == booking.getTotalAmount() * 0.5));
    }

    @Test
    void cancelBooking_WithFullRefund() {
        // Arrange
        schedule.setDepartureTime(LocalDateTime.now().plusDays(2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(argThat(b -> 
            "CANCELLED".equals(b.getStatus()) && 
            b.getTotalAmount() == booking.getTotalAmount()));
    }

    @Test
    void cancelBooking_WithNoRefund() {
        // Arrange
        schedule.setDepartureTime(LocalDateTime.now().plusMinutes(30));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(argThat(b -> 
            "CANCELLED".equals(b.getStatus()) && 
            b.getTotalAmount() == 0.0));
    }

    @Test
    void cancelBooking_WithMultipleSeats() {
        // Arrange
        int initialAvailableSeats = 40;
        int bookedSeats = 5;
        schedule.setAvailableSeats(initialAvailableSeats - bookedSeats);
        booking.setNumberOfSeats(bookedSeats);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
            Schedule savedSchedule = invocation.getArgument(0);
            assertEquals(initialAvailableSeats, savedSchedule.getAvailableSeats());
            return savedSchedule;
        });

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void cancelBooking_WithScheduleUpdate() {
        // Arrange
        int initialAvailableSeats = 40;
        int bookedSeats = 1;
        schedule.setAvailableSeats(initialAvailableSeats - bookedSeats);
        booking.setNumberOfSeats(bookedSeats);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> {
            Schedule savedSchedule = invocation.getArgument(0);
            assertEquals(initialAvailableSeats, savedSchedule.getAvailableSeats());
            return savedSchedule;
        });

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void cancelBooking_WithConcurrentScheduleUpdate() throws InterruptedException {
        // Arrange
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successfulUpdates = new AtomicInteger(0);

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    bookingService.cancelBooking(1L);
                    successfulUpdates.incrementAndGet();
                } catch (Exception e) {
                    // Expected for some threads
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Assert
        assertEquals(1, successfulUpdates.get());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void cancelBooking_WithNonExistentBooking() {
        // Arrange
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            bookingService.cancelBooking(999L));
    }

    @Test
    void cancelBooking_WithAlreadyCancelledBooking() {
        // Arrange
        booking.setStatus("CANCELLED");
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            bookingService.cancelBooking(1L));
    }

    @Test
    void cancelBooking_WithPastSchedule() {
        // Arrange
        schedule.setDepartureTime(LocalDateTime.now().minusHours(1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            bookingService.cancelBooking(1L));
    }

    @Test
    void cancelBooking_WithZeroAmount() {
        // Arrange
        booking.setTotalAmount(0.0);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        // Act
        bookingService.cancelBooking(1L);

        // Assert
        verify(bookingRepository).save(argThat(b -> 
            "CANCELLED".equals(b.getStatus()) && 
            b.getTotalAmount() == 0.0));
    }
} 