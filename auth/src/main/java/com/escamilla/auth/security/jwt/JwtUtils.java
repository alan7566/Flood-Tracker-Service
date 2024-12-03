package com.escamilla.auth.security.jwt;

import com.escamilla.auth.model.entity.User;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@PropertySource("classpath:application.properties")
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.jwt.secret.key}")
    private String jwtSecret;

    @Value("${security.jwt.expiration.ms}")
    private int jwtExpirationMs;

    public String generateJwtToken(User user) {
        logger.debug("JwtUtils: Generating JWT for user: {}", user.getUsername());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        logger.debug("JwtUtils: Extracting username from JWT");
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            logger.debug("JwtUtils: Validating JWT");
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
}