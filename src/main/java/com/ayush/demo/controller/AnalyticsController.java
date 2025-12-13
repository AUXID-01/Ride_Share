package com.ayush.demo.controller;

import com.ayush.demo.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analytics;

    public AnalyticsController(AnalyticsService a) { this.analytics = a; }

    @GetMapping("/driver/{driver}/earnings")
    public Double earnings(@PathVariable String driver) {
        return analytics.totalEarnings(driver);
    }
}
