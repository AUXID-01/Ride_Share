package com.example.rideshare.service;

import com.example.rideshare.model.Ride;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class RideQueryServiceV2 {

    @SuppressWarnings("unused")
    private final MongoTemplate mongoTemplate;

    public RideQueryServiceV2(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * API 2: Filter rides by distance range (min-max)
     */
    public List<Ride> filterByDistance(Double min, Double max) {
        Criteria criteria = Criteria.where("distanceKm").gte(min).lte(max);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Ride.class);
    }

    /**
     * API 3: Filter rides between date range
     */
    public List<Ride> filterByDateRange(LocalDate start, LocalDate end) {
        Criteria criteria = Criteria.where("createdDate").gte(start).lte(end);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Ride.class);
    }

    /**
     * API 4: Sort rides by fare amount
     */
    public List<Ride> sortByFare(String order) {
        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "fare");

        Query query = new Query().with(sort);

        return mongoTemplate.find(query, Ride.class);
    }

    /**
     * API 1: Search rides by pickup OR drop location
     */
    public List<Ride> searchRides(String text) {
        Pattern pattern = Pattern.compile(text, Pattern.CASE_INSENSITIVE);

        Criteria criteria = new Criteria().orOperator(
                Criteria.where("pickupLocation").regex(pattern),
                Criteria.where("dropLocation").regex(pattern)
        );

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Ride.class);
    }

    /**
     * API 8: Filter rides by status and keyword search (AND + OR combo)
     */
    public List<Ride> filterByStatusAndSearch(String status, String search) {

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // 🔍 Add search condition if provided
        if (search != null && !search.isBlank()) {
            Pattern pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("pickupLocation").regex(pattern),
                    Criteria.where("dropLocation").regex(pattern)
            ));
        }

        // 🔍 Add status condition only if provided
        if (status != null && !status.isBlank()) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        // Combine criteria (AND all conditions)
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Ride.class);
    }

    /**
     * API 9: Advanced search with multiple criteria + pagination
     */
    public List<Ride> advancedSearch(String search, String status, String sortField,
                                     String order, int page, int size) {

        List<Criteria> criteriaList = new ArrayList<>();

        // Safe search handling
        if (search != null && !search.isBlank()) {
            Pattern pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("pickupLocation").regex(pattern),
                    Criteria.where("dropLocation").regex(pattern)
            ));
        }

        if (status != null && !status.isBlank()) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        Criteria finalCriteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(finalCriteria);

        Sort.Direction direction = order.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        query.with(Sort.by(direction, sortField));
        query.with(PageRequest.of(page, size));

        return mongoTemplate.find(query, Ride.class);
    }

    /**
     * API 7: Get active rides for a driver (status = ACCEPTED)
     */
    public List<Ride> getDriverActiveRides(String driverId) {
        Criteria criteria = Criteria.where("driverId").is(driverId)
                .and("status").is("ACCEPTED");

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Ride.class);
    }
}
