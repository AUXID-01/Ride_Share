package com.ayush.demo.controller;

import com.ayush.demo.model.Ride;
import com.ayush.demo.service.RideService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService service;

    public RideController(RideService s) { this.service = s; }

    @PostMapping
    public Ride createRide(@AuthenticationPrincipal UserDetails user,
                           @RequestBody Ride ride) {
        return service.createRide(user.getUsername(), ride);
    }

    @PostMapping("/accept/{id}")
    public Ride accept(@AuthenticationPrincipal UserDetails driver,
                       @PathVariable String id) {
        return service.acceptRide(id, driver.getUsername());
    }

    @PostMapping("/complete/{id}")
    public Ride complete(@PathVariable String id) {
        return service.completeRide(id);
    }
}
