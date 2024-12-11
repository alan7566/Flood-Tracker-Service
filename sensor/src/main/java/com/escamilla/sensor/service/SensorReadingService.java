package com.escamilla.sensor.service;

import com.escamilla.sensor.repository.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SensorReadingService {
    @Autowired
    private SensorReadingRepository sensorReadingRepository;


    public ResponseEntity<?> getSensorReadings() {
        return ResponseEntity.ok(sensorReadingRepository.findAll());
    }

    public ResponseEntity<?> getSensorReadingById(String id) {
        return sensorReadingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
