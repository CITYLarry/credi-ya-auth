package com.crediya.auth.infrastructure.driven.security;

import com.crediya.auth.domain.model.User;
import com.crediya.auth.domain.ports.out.TokenProviderPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Driven adapter that implements the TokenProviderPort using the JJWT library.
 * It is responsible for creating signed JSON Web Tokens.
 */
@Slf4j
@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey key;

    /**
     * This method is executed after the bean is created and properties are injected.
     * It decodes the Base64 secret key into a secure Key object suitable for signing tokens.
     */
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT Secret Key initialized successfully.");
    }

    /**
     * Generates a JWT for a given user.
     * The token includes the user's email as the subject, and their role and name as custom claims.
     *
     * @param user The authenticated User domain object.
     * @return A signed, URL-safe JWT string.
     */
    @Override
    public String generateToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().getName())
                .claim("fullName", user.getFirstName() + " " + user.getLastName())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
