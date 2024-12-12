package com.escamilla.sensor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthValidationService {
    @Value("${auth.service.url}") // Configura la URL del módulo "auth" en application.properties
    private String authServiceUrl;

    private final RestTemplate restTemplate;

    public AuthValidationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean validateToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();

            if (!token.startsWith("Bearer ")) {
                // Solo agrega "Bearer" si no está ya presente
                token = "Bearer " + token;
            }

            headers.set("Authorization", token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);


            ResponseEntity<Void> response = restTemplate.exchange(
                    authServiceUrl + "/auth/validate",
                    HttpMethod.GET,
                    entity,
                    Void.class
            );

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}
