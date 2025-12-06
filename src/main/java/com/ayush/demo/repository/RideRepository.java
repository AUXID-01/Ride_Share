package com.ayush.demo.repository;


import com.ayush.demo.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RideRepository extends MongoRepository<Ride, String> {

    List<Ride> findByUserId(String userId);

    List<Ride> findByStatus(String status);
}