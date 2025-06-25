package com.busbooking.service;

import com.busbooking.entity.Booking;
import com.busbooking.entity.Schedule;
import com.busbooking.entity.User;
import com.busbooking.repository.BookingRepository;
import com.busbooking.repository.ScheduleRepository;
import com.busbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    private final ConcurrentHashMap<Long, ReentrantLock> scheduleLocks = new ConcurrentHashMap<>();

    private ReentrantLock getLockForSchedule(Long scheduleId) {
        return scheduleLocks.computeIfAbsent(scheduleId, k -> new ReentrantLock());
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional
    public Booking createBooking(Long scheduleId, Long userId, Integer numberOfSeats) {
        if (numberOfSeats <= 0) {
            throw new IllegalArgumentException("Number of seats must be greater than 0");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if schedule is in the past or about to depart
        if (schedule.getDepartureTime().isBefore(LocalDateTime.now().plusMinutes(30))) {
            throw new IllegalStateException("Cannot book a schedule that is in the past or about to depart");
        }

        // Check if user has too many active bookings
        List<Booking> userActiveBookings = bookingRepository.findByUserId(userId);
        if (userActiveBookings.size() >= 5) {
            throw new IllegalStateException("User has reached maximum number of active bookings");
        }

        // Check for overlapping bookings
        for (Booking existingBooking : userActiveBookings) {
            if ("CONFIRMED".equals(existingBooking.getStatus())) {
                Schedule existingSchedule = existingBooking.getSchedule();
                if (isOverlapping(schedule, existingSchedule)) {
                    throw new IllegalStateException("User has an overlapping booking");
                }
            }
        }

        ReentrantLock lock = getLockForSchedule(scheduleId);
        try {
            if (!lock.tryLock()) {
                throw new RuntimeException("Schedule is currently being booked by another user");
            }

            // Recheck available seats after acquiring lock
            schedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));
            
            if (schedule.getAvailableSeats() < numberOfSeats) {
                throw new RuntimeException("Not enough seats available");
            }

            Booking booking = new Booking();
            booking.setSchedule(schedule);
            booking.setUser(user);
            booking.setNumberOfSeats(numberOfSeats);
            booking.setTotalAmount(schedule.getFare() * numberOfSeats);
            booking.setBookingDate(LocalDateTime.now());
            booking.setStatus("CONFIRMED");

            schedule.setAvailableSeats(schedule.getAvailableSeats() - numberOfSeats);
            scheduleRepository.save(schedule);

            return bookingRepository.save(booking);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private boolean isOverlapping(Schedule newSchedule, Schedule existingSchedule) {
        LocalDateTime newDeparture = newSchedule.getDepartureTime();
        LocalDateTime newArrival = newSchedule.getArrivalTime();
        LocalDateTime existingDeparture = existingSchedule.getDepartureTime();
        LocalDateTime existingArrival = existingSchedule.getArrivalTime();

        // Check if the new schedule overlaps with the existing schedule
        return !(newArrival.isBefore(existingDeparture) || newDeparture.isAfter(existingArrival));
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        Schedule schedule = booking.getSchedule();
        if (schedule.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot cancel a booking for a past schedule");
        }

        ReentrantLock lock = getLockForSchedule(schedule.getId());
        try {
            if (!lock.tryLock()) {
                throw new RuntimeException("Schedule is currently being modified by another user");
            }

            // Calculate refund based on cancellation time
            double refundAmount = calculateRefundAmount(booking);
            booking.setTotalAmount(refundAmount);

            schedule.setAvailableSeats(schedule.getAvailableSeats() + booking.getNumberOfSeats());
            scheduleRepository.save(schedule);

            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private double calculateRefundAmount(Booking booking) {
        LocalDateTime departureTime = booking.getSchedule().getDepartureTime();
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilDeparture = java.time.Duration.between(now, departureTime).toHours();

        if (hoursUntilDeparture >= 24) {
            // Full refund if cancelled 24 hours or more before departure
            return booking.getTotalAmount();
        } else if (hoursUntilDeparture >= 12) {
            // 50% refund if cancelled between 12-24 hours before departure
            return booking.getTotalAmount() * 0.5;
        } else {
            // No refund if cancelled less than 12 hours before departure
            return 0.0;
        }
    }
} 