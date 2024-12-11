package com.escamilla.sensor.repository;

import com.escamilla.sensor.model.SensorReading;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SensorReadingRepository extends MongoRepository<SensorReading, String> {
    Optional<SensorReading> findById(String id);

}
