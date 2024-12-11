package com.escamilla.sensor.controller;

import com.escamilla.sensor.service.SensorReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SensorReadingController {
    @Autowired
    private SensorReadingService sensorReadingService;

    @GetMapping("/readings")
    public ResponseEntity<?> getSensorReadings() {
        return sensorReadingService.getSensorReadings();
    }

    @GetMapping("/reading/{id}")
    public ResponseEntity<?> getSensorReadingById(@PathVariable String id) {
        return sensorReadingService.getSensorReadingById(id);
    }

}
