package com.danceylone.backend.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final String secret;

    public JwtAuthenticationFilter(String secret) {
        this.secret = secret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws java.io.IOException, jakarta.servlet.ServletException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(
                                Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))
                        )
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String subject = claims.getSubject();
                
                // Only create authentication if subject is not null
                if (subject != null && !subject.isBlank()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    subject,
                                    null,
                                    List.of()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.debug("JWT token has null or blank subject, skipping authentication");
                }

            } catch (ExpiredJwtException e) {
                log.warn("JWT token has expired: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                log.warn("Malformed JWT token: {}", e.getMessage());
            } catch (SignatureException e) {
                log.warn("Invalid JWT signature: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.warn("Unsupported JWT token: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
            } catch (Exception e) {
                // Log unexpected exceptions and rethrow to avoid silently swallowing bugs
                log.error("Unexpected error during JWT validation", e);
                throw e;
            }
        }

        filterChain.doFilter(request, response);
    }
}