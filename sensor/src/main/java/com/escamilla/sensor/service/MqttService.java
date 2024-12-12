package com.escamilla.sensor.service;

import com.escamilla.sensor.model.SensorReading;
import com.escamilla.sensor.model.SensorType;
import com.escamilla.sensor.repository.SensorReadingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class MqttService {

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return message -> {
            // Este bean procesa el mensaje recibido del broker MQTT
            String payload = (String) message.getPayload();
            String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
            assert topic != null;
            String subtopic = topic.substring(topic.lastIndexOf('/') + 1);
            SensorType sensorType = SensorType.valueOf(subtopic.toUpperCase());
            System.out.println("Mensaje recibido en topic [" + topic + "]: " + payload);

            Map<String, Object> sensorData;
            try {
                sensorData = objectMapper.readValue(payload, Map.class);
            } catch (JsonProcessingException e) {
                // Handle non-JSON payloads
                Object value;
                try {
                    value = Double.parseDouble(payload);
                } catch (NumberFormatException ex) {
                    value = payload;
                }
                sensorData = Map.of("value", value);
            }

            if (!isValidSensorData(sensorType, sensorData)) {
                System.out.println("Invalid sensor data: " + sensorData);
                return;
            }

            // Guardar el mensaje en la base de datos
            SensorReading sensorReading = new SensorReading();
            sensorReading.setSensorType(sensorType);
            sensorReading.setSensorData(sensorData);
            sensorReadingRepository.save(sensorReading);
        };
    }

    private boolean isValidSensorData(SensorType sensorType, Map<String, Object> sensorData) {
        try {
            return switch (sensorType) {
                case HUMIDITY, DISTANCE, RAIN -> {
                    Object data = sensorData.get("value");
                    yield data instanceof Double || data instanceof Integer;
                }
            };
        } catch (ClassCastException e) {
            return false;
        }
    }
}