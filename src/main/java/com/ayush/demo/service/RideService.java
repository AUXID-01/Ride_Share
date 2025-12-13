package com.ayush.demo.service;

import com.ayush.demo.model.Ride;
import com.ayush.demo.repository.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RideService {

    private final RideRepository repo;

    public RideService(RideRepository repo) {
        this.repo = repo;
    }

    public Ride createRide(String passenger, Ride ride) {
        ride.setPassengerUsername(passenger);
        ride.setStatus("REQUESTED");
        return repo.save(ride);
    }

    @Transactional
    public Ride acceptRide(String id, String driver) {
        Ride r = repo.findById(id).orElseThrow();
        r.setDriverUsername(driver);
        r.setStatus("ACCEPTED");
        return repo.save(r);
    }

    public Ride completeRide(String id) {
        Ride r = repo.findById(id).orElseThrow();
        r.setStatus("COMPLETED");
        return repo.save(r);
    }
}
