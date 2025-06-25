package com.busbooking.service;

import com.busbooking.entity.Bus;
import com.busbooking.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BusService {
    @Autowired
    private BusRepository busRepository;

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus getBusById(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));
    }

    public Bus createBus(Bus bus) {
        return busRepository.save(bus);
    }

    public Bus updateBus(Long id, Bus busDetails) {
        Bus bus = getBusById(id);
        bus.setBusNumber(busDetails.getBusNumber());
        bus.setBusName(busDetails.getBusName());
        bus.setTotalSeats(busDetails.getTotalSeats());
        bus.setBusType(busDetails.getBusType());
        return busRepository.save(bus);
    }

    public void deleteBus(Long id) {
        Bus bus = getBusById(id);
        busRepository.delete(bus);
    }
} 