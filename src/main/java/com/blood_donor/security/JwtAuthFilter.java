package com.blood_donor.security;


import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @org.springframework.beans.factory.annotation.Autowired
    public JwtAuthFilter(
            JwtTokenProvider jwtTokenProvider,
            CustomUserDetailsService customUserDetailsService,
            @org.springframework.beans.factory.annotation.Autowired(required = false) org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.redisTemplate = redisTemplate;
    }

    public void setRedisTemplate(org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        // Bypass refresh token endpoint to prevent expired access tokens from blocking requests
        String path = request.getRequestURI();
        if ("/api/auth/refresh".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        //Extract Bearer token from Authorization header
        String token = extractTokenFromRequest(request);
        if (StringUtils.hasText(token)){
            log.info("JwtAuthFilter checking token blacklist. Token present: {}, redisTemplate present: {}", 
                     token != null, redisTemplate != null);
            if (redisTemplate != null) {
                try {
                    if (Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:token:" + token))) {
                        log.warn("Attempted request with blacklisted access token");
                        SecurityResponseHelper.writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                        return;
                    }
                } catch (Exception e) {
                    log.error("Redis token blacklist check failed, fail-closed policy enforced: {}", e.getMessage());
                    SecurityResponseHelper.writeError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Authentication service temporarily unavailable (blacklist check failure)");
                    return;
                }
            }
            try {
                String email = jwtTokenProvider.extractEmail(token);
                //Only Authenticate if not already authenticate
                if(email !=null && SecurityContextHolder.getContext().getAuthentication()==null){
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                    if (jwtTokenProvider.validateToken(token, email)){
                        //Creation Authentication object
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                                .buildDetails(request));
                        //Set in security context
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }catch (Exception e){
                log.error("Jwt Authentication failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    //Extract token from Authorization: Bear <token>
    private String extractTokenFromRequest(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if ((StringUtils.hasText(token)) && token.startsWith("Bearer ")){
            return token.substring(7);
        }
        return null;
    }
}
