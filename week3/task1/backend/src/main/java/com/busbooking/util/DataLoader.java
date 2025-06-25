package com.busbooking.util;

import com.busbooking.entity.Bus;
import com.busbooking.entity.Schedule;
import com.busbooking.repository.BookingRepository;
import com.busbooking.repository.BusRepository;
import com.busbooking.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public void run(String... args) throws Exception {
        // Clear existing data in correct order to respect foreign keys
        bookingRepository.deleteAll(); // Delete bookings first
        scheduleRepository.deleteAll(); // Then schedules
        busRepository.deleteAll(); // Then buses

        // Create some dummy buses
        Bus bus1 = new Bus();
        bus1.setBusNumber("KA01AB1234");
        bus1.setBusName("Express Transit");
        bus1.setTotalSeats(40);
        bus1.setBusType("AC Sleeper");
        busRepository.save(bus1);

        Bus bus2 = new Bus();
        bus2.setBusNumber("KL05CD5678");
        bus2.setBusName("Luxury Cruiser");
        bus2.setTotalSeats(30);
        bus2.setBusType("Non-AC Seater");
        busRepository.save(bus2);

        Bus bus3 = new Bus();
        bus3.setBusNumber("TN10EF9012");
        bus3.setBusName("City Link");
        bus3.setTotalSeats(50);
        bus3.setBusType("AC Seater");
        busRepository.save(bus3);

        // Create some dummy schedules
        Schedule schedule1 = new Schedule();
        schedule1.setBus(bus1);
        schedule1.setSource("Bangalore");
        schedule1.setDestination("Chennai");
        schedule1.setDepartureTime(LocalDateTime.now().plusHours(2));
        schedule1.setArrivalTime(LocalDateTime.now().plusHours(9));
        schedule1.setFare(1200.00);
        schedule1.setAvailableSeats(bus1.getTotalSeats());
        scheduleRepository.save(schedule1);

        Schedule schedule2 = new Schedule();
        schedule2.setBus(bus1);
        schedule2.setSource("Bangalore");
        schedule2.setDestination("Hyderabad");
        schedule2.setDepartureTime(LocalDateTime.now().plusDays(1).plusHours(10));
        schedule2.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(20));
        schedule2.setFare(1500.00);
        schedule2.setAvailableSeats(bus1.getTotalSeats() - 5); // Some seats already booked
        scheduleRepository.save(schedule2);

        Schedule schedule3 = new Schedule();
        schedule3.setBus(bus2);
        schedule3.setSource("Kochi");
        schedule3.setDestination("Trivandrum");
        schedule3.setDepartureTime(LocalDateTime.now().plusHours(3));
        schedule3.setArrivalTime(LocalDateTime.now().plusHours(6));
        schedule3.setFare(450.00);
        schedule3.setAvailableSeats(bus2.getTotalSeats());
        scheduleRepository.save(schedule3);

        Schedule schedule4 = new Schedule();
        schedule4.setBus(bus3);
        schedule4.setSource("Chennai");
        schedule4.setDestination("Bangalore");
        schedule4.setDepartureTime(LocalDateTime.now().plusDays(2).plusHours(8));
        schedule4.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(15));
        schedule4.setFare(1100.00);
        schedule4.setAvailableSeats(bus3.getTotalSeats());
        scheduleRepository.save(schedule4);
    }
} 