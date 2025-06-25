package com.busbooking.repository;

import com.busbooking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findBySourceAndDestination(String source, String destination);
} 