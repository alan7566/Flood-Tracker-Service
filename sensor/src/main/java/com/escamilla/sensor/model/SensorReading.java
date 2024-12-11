package com.escamilla.sensor.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Document(collection = "sensor_readings")
public class SensorReading {
    @Id
    private String id;

    @NotNull(message = "Tipo de sensor es requerido")
    private SensorType sensorType;

    @NotNull(message = "Timestamp es requerido")
    private Instant timestamp;

    @NotNull(message = "Datos del sensor son requeridos")
    private Map<String, Object> sensorData;

    // Constructor
    public SensorReading() {
        this.timestamp = Instant.now();
        this.sensorData = new HashMap<>();
    }

    // Equals y HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorReading that = (SensorReading) o;
        return Objects.equals(id, that.id) &&
                sensorType == that.sensorType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sensorType);
    }
}