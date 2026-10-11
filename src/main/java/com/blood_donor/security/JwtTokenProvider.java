package com.blood_donor.security;

import com.blood_donor.common.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    //Generate secret key from string
    private SecretKey getSignInKey(){
         byte[] keyByte = jwtSecret.getBytes(StandardCharsets.UTF_8);
         if (keyByte.length < 32){
             throw new IllegalStateException(
                     "Jwt secret must be at least 32 characters long"
             );
         }
        return Keys.hmacShaKeyFor(keyByte);
    }

    //Generate JWT Access Token
    //contains: email, role, userId
    public String generateAccessToken(String email, Set<Role> roles, Long userId){
        List<String> roleNames = roles.stream()
                .map(Role::name)
                .toList();
        return Jwts.builder()
                .claims(Map.of(
                "roles", roleNames,
                        "userId", userId))
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .signWith(getSignInKey()).compact();
    }

    //Generate refresh token
    public String generateRefreshToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+refreshExpiration))
                .signWith(getSignInKey()).compact();
    }
    //Extract email (subject) from token
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }
    //Extract userId
    public Long extractUserId(String token){
        return extractClaim(token, claims -> ((Number) claims.get("userId")).longValue());
    }

    //Extract roles
    public Set<String> extractRoles(String token){
        return extractClaim(token, claims -> {
            Object roles = claims.get("roles");
            if (roles instanceof List<?> list){
                return list.stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet());
            }
            return Set.of();
        });
    }
    //Extract any claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims =  extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //Validate token: if token valid and not expire so return true
    public boolean validateToken(String token, String email){
        try {
            String tokenEmail = extractEmail(token);
            return tokenEmail.equals(email) && !isTokenExpired(token);
        }catch (JwtException | IllegalArgumentException exception){
            log.warn("Invalid JWT token {}", exception.getMessage());
            return false;
        }
    }
    //Expiry check
    private boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    //extract all claims
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (ExpiredJwtException e){
            log.warn("Jwt token expired: {}",e.getMessage());
            throw e;
        }catch (MalformedJwtException e){
            log.warn("Malformed Jwt Token: {}", e.getMessage());
            throw e;
        }catch (JwtException e){
            log.warn("Invalid Jwt token: {}", e.getMessage());
            throw e;
        }
    }
}
