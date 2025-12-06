package com.ayush.demo.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import com.ayush.demo.model.Ride;
import com.ayush.demo.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RideController {

    @Autowired
    private RideService rideService;

    // Create Ride (Passenger)
    @PostMapping("/rides")
    public ResponseEntity<?> createRide(@Valid @RequestBody Map<String, String> body) {
        String pickup = body.get("pickupLocation");
        String drop = body.get("dropLocation");
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Ride ride = rideService.createRide(userId, pickup, drop);
        return ResponseEntity.ok(ride);
    }

    // View My Rides (Passenger)
    @GetMapping("/user/rides")
    public ResponseEntity<?> getUserRides() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Ride> rides = rideService.getRidesByUser(userId);
        return ResponseEntity.ok(rides);
    }

    // View Pending Rides (Driver)
    @GetMapping("/driver/rides/requests")
    public ResponseEntity<?> getPendingRides() {
        List<Ride> rides = rideService.getPendingRides();
        return ResponseEntity.ok(rides);
    }

    // Driver Accepts Ride
    @PostMapping("/driver/rides/{rideId}/accept")
    public ResponseEntity<?> acceptRide(@PathVariable String rideId) {
        String driverId = SecurityContextHolder.getContext().getAuthentication().getName();
        Ride ride = rideService.acceptRide(rideId, driverId);
        return ResponseEntity.ok(ride);
    }

    // Complete Ride (Driver or User)
    @PostMapping("/rides/{rideId}/complete")
    public ResponseEntity<?> completeRide(@PathVariable String rideId) {
        Ride ride = rideService.completeRide(rideId);
        return ResponseEntity.ok(ride);
    }
}