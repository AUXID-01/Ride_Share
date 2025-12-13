package com.example.rideshare.service;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.when;

@Service
public class RideAnalyticsServiceV2 {

    private final MongoTemplate mongoTemplate;

    public RideAnalyticsServiceV2(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * API 10: Rides per day (for charts, trends etc.)
     */
    public List<Document> ridesPerDay() {

        Aggregation agg = newAggregation(
                project("createdDate")
                        .andExpression("{$dateToString: {format: '%Y-%m-%d', date: '$createdDate'}}")
                        .as("day"),

                group("day")
                        .count().as("rideCount"),

                sort(Sort.by(Sort.Direction.ASC, "_id")),

                project("rideCount")
                        .and("_id").as("day")
                        .andExclude("_id")
        );

        return mongoTemplate
                .aggregate(agg, "rides", Document.class)
                .getMappedResults();
    }

    /**
     * API 11: Driver summary - total rides, completed, cancelled, avg distance, total fare
     */
    public Map<String, Object> getDriverStats(String driverId) {

        Aggregation agg = newAggregation(
                match(Criteria.where("driverId").is(driverId)),

                group("driverId")
                        .count().as("totalRides")
                        .sum(when(Criteria.where("status").is("COMPLETED")).then(1).otherwise(0))
                        .as("completedRides")
                        .sum(when(Criteria.where("status").is("CANCELLED")).then(1).otherwise(0))
                        .as("cancelledRides")
                        .avg("distanceKm").as("avgDistanceKm")
                        .sum("fare").as("totalFare"),

                project("totalRides", "completedRides", "cancelledRides",
                        "avgDistanceKm", "totalFare")
        );

        Document result = mongoTemplate
                .aggregate(agg, "rides", Document.class)
                .getUniqueMappedResult();

        if (result == null) {
            return Map.of(
                    "totalRides", 0,
                    "completedRides", 0,
                    "cancelledRides", 0,
                    "avgDistanceKm", 0,
                    "totalFare", 0
            );
        }

        return result;
    }

    /**
     * API 12: User spending summary - total completed rides & total fare
     */
    public Map<String, Object> getUserSpending(String userId) {

        Aggregation agg = newAggregation(
                match(new Criteria().andOperator(
                        Criteria.where("userId").is(userId),
                        Criteria.where("status").is("COMPLETED")
                )),

                group("userId")
                        .count().as("totalCompletedRides")
                        .sum("fare").as("totalFare"),

                project("totalCompletedRides", "totalFare")
        );

        Document result = mongoTemplate
                .aggregate(agg, "rides", Document.class)
                .getUniqueMappedResult();

        if (result == null) {
            return Map.of(
                    "totalCompletedRides", 0,
                    "totalFare", 0
            );
        }

        return result;
    }

    /**
     * API 13: Status summary - count rides grouped by status
     */
    public List<Document> getStatusSummary() {

        Aggregation agg = newAggregation(
                group("status")
                        .count().as("count"),

                sort(Sort.by(Sort.Direction.DESC, "count")),

                project("count")
                        .and("_id").as("status")
                        .andExclude("_id")
        );

        return mongoTemplate
                .aggregate(agg, "rides", Document.class)
                .getMappedResults();
    }
}
