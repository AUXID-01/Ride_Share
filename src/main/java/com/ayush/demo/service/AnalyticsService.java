package com.ayush.demo.service;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;

@Service
public class AnalyticsService {

    private final MongoTemplate template;

    public AnalyticsService(MongoTemplate template) {
        this.template = template;
    }

    public Double totalEarnings(String driver) {
        MatchOperation match = match(Criteria.where("driverUsername").is(driver));
        GroupOperation group = group().sum("fare").as("total");
        Aggregation agg = newAggregation(match, group);

        Document result = template.aggregate(agg, "rides", Document.class)
                .getUniqueMappedResult();

        return result != null ? result.getDouble("total") : 0.0;
    }
}
