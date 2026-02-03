package com.danceylone.backend.shared.security;

import com.danceylone.backend.shared.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

@Service
public class JwtTokenService {

    private final JwtProperties props;

    public JwtTokenService(JwtProperties props) {
        this.props = props;
    }

    public String generateToken(String subject, Set<String> roles) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + props.getExpirationMillis());

        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(
                        Keys.hmacShaKeyFor(
                                props.getSecret().getBytes(StandardCharsets.UTF_8)
                        ),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }
}