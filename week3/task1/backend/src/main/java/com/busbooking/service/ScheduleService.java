package com.busbooking.service;

import com.busbooking.entity.Schedule;
import com.busbooking.repository.ScheduleRepository;
import com.busbooking.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private BusRepository busRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    public List<Schedule> searchSchedules(String source, String destination) {
        return scheduleRepository.findBySourceAndDestination(source, destination);
    }

    public Schedule createSchedule(Schedule schedule) {
        if (schedule.getBus() == null || schedule.getBus().getId() == null) {
            throw new RuntimeException("Bus is required");
        }
        
        if (!busRepository.existsById(schedule.getBus().getId())) {
            throw new RuntimeException("Bus not found");
        }
        
        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Long id, Schedule scheduleDetails) {
        Schedule schedule = getScheduleById(id);
        
        if (scheduleDetails.getBus() != null && scheduleDetails.getBus().getId() != null) {
            if (!busRepository.existsById(scheduleDetails.getBus().getId())) {
                throw new RuntimeException("Bus not found");
            }
            schedule.setBus(scheduleDetails.getBus());
        }
        
        schedule.setSource(scheduleDetails.getSource());
        schedule.setDestination(scheduleDetails.getDestination());
        schedule.setDepartureTime(scheduleDetails.getDepartureTime());
        schedule.setArrivalTime(scheduleDetails.getArrivalTime());
        schedule.setFare(scheduleDetails.getFare());
        schedule.setAvailableSeats(scheduleDetails.getAvailableSeats());
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        Schedule schedule = getScheduleById(id);
        scheduleRepository.delete(schedule);
    }
} 