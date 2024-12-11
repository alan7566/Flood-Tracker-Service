package com.escamilla.sensor.controller;

import com.escamilla.sensor.service.SensorReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensor")
public class SensorReadingController {
    @Autowired
    private SensorReadingService sensorReadingService;

    @GetMapping("/readings")
    public ResponseEntity<?> getSensorReadings() {
        return sensorReadingService.getSensorReadings();
    }

    @GetMapping("/reading/{id}")
    public ResponseEntity<?> getSensorReadingById(String id) {
        return sensorReadingService.getSensorReadingById(id);
    }

}
