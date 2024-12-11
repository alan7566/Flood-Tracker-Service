package com.escamilla.sensor.controller;

import com.escamilla.sensor.service.AuthValidationService;
import com.escamilla.sensor.service.SensorReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SensorReadingController {
    @Autowired
    private SensorReadingService sensorReadingService;

    @Autowired
    private AuthValidationService authValidationService;

    @GetMapping("/readings")
    public ResponseEntity<?> getSensorReadings(@RequestHeader("Authorization") String token) {
        if (!authValidationService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied: Invalid Token");
        }
        return sensorReadingService.getSensorReadings();
    }

    @GetMapping("/reading/{id}")
    public ResponseEntity<?> getSensorReadingById(
            @PathVariable String id,
            @RequestHeader("Authorization") String token
    ) {
        System.out.println("Token received in controller: " + token);
        if (!authValidationService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied: Invalid Token");
        }
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid ID: ID cannot be null or empty");
        }
        return sensorReadingService.getSensorReadingById(id);
    }

}
