package com.ayush.demo.service;

import com.ayush.demo.exception.BadRequestException;
import com.ayush.demo.exception.NotFoundException;
import com.ayush.demo.model.Ride;
import com.ayush.demo.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    // Create Ride (Passenger)
    public Ride createRide(String userId, String pickupLocation, String dropLocation) {
        if (pickupLocation.isBlank() || dropLocation.isBlank()) {
            throw new BadRequestException("Pickup and Drop locations are required");
        }

        Ride ride = new Ride(userId, pickupLocation, dropLocation);
        return rideRepository.save(ride);
    }

    // Get User's own rides
    public List<Ride> getRidesByUser(String userId) {
        return rideRepository.findByUserId(userId);
    }

    // Get all pending rides for driver
    public List<Ride> getPendingRides() {
        return rideRepository.findByStatus("REQUESTED");
    }

    // Driver accepts a ride
    public Ride acceptRide(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!ride.getStatus().equals("REQUESTED")) {
            throw new BadRequestException("Ride is not in REQUESTED status");
        }

        ride.setDriverId(driverId);
        ride.setStatus("ACCEPTED");

        return rideRepository.save(ride);
    }

    // Complete ride (Driver or User)
    public Ride completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!ride.getStatus().equals("ACCEPTED")) {
            throw new BadRequestException("Ride is not in ACCEPTED status");
        }

        ride.setStatus("COMPLETED");

        return rideRepository.save(ride);
    }
}
