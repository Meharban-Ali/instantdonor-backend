package com.blood_donor.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ═══════════════════════════════════════════════════════════
 * RATE LIMITING FILTER
 * ═══════════════════════════════════════════════════════════
 *
 * Implements a lightweight IP-based Token Bucket rate limiting
 * algorithm for auth/security sensitive endpoints (/api/auth/login,
 * /api/auth/register, /api/auth/forgot-password, /api/auth/reset-password).
 * Utilizes Redis for distributed deployments, with a transparent
 * local ConcurrentHashMap fallback.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Value("${rate-limiting.enabled:true}")
    private boolean enabled;

    @Value("${rate-limiting.capacity:100}")
    private long capacity;

    @Value("${rate-limiting.login.ip-capacity:10}")
    private long loginIpCapacity;

    @Value("${rate-limiting.refill-interval:60}")
    private long refillIntervalSeconds;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private com.blood_donor.monitoring.CustomMetricsService customMetricsService;

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {

        log.debug("RateLimitingFilter: uri={}, enabled={}, capacity={}, loginIpCapacity={}, refillInterval={}",
                request.getRequestURI(), enabled, capacity, loginIpCapacity, refillIntervalSeconds);

        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI().substring(request.getContextPath().length());

        boolean isLogin = uri.equals("api/auth/login") || uri.equals("/api/auth/login");
        // We only rate limit specific authentication endpoints
        boolean isAuthEndpoint = isLogin ||
                uri.equals("api/auth/register") || uri.equals("/api/auth/register") ||
                uri.equals("api/auth/forgot-password") || uri.equals("/api/auth/forgot-password") ||
                uri.equals("api/auth/reset-password") || uri.equals("/api/auth/reset-password");

        if (isAuthEndpoint) {
            String ip = getClientIp(request);
            long effectiveCapacity = isLogin ? loginIpCapacity : capacity;
            String key = isLogin ? "rate:limit:login:ip:" + ip : "rate:limit:auth:" + ip;
            boolean allowed;

            if (redisTemplate != null) {
                try {
                    allowed = tryConsumeRedis(key, effectiveCapacity);
                } catch (Exception e) {
                    log.warn("Redis rate limiting failed, falling back to local memory rate limiting. Key: {}. Error: {}", key, e.getMessage());
                    allowed = tryConsumeLocal(key, effectiveCapacity);
                }
            } else {
                allowed = tryConsumeLocal(key, effectiveCapacity);
            }

            if (!allowed) {
                log.warn("Rate limit exceeded for key: {} on URI: {}", key, uri);
                if (this.customMetricsService != null) {
                    this.customMetricsService.recordRateLimit429();
                }
                response.setHeader("Retry-After", String.valueOf(this.refillIntervalSeconds));
                SecurityResponseHelper.writeError(response, 429, "Too many requests. Please try again later.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean tryConsumeLocal(String key, long targetCapacity) {
        double refillRatePerMs = (double) targetCapacity / (refillIntervalSeconds * 1000.0);
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(targetCapacity));
        return bucket.tryConsume(refillRatePerMs);
    }

    private synchronized boolean tryConsumeRedis(String key, long targetCapacity) {
        long now = System.currentTimeMillis();
        double refillRatePerMs = (double) targetCapacity / (refillIntervalSeconds * 1000.0);

        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
        double tokens;
        long lastRefillTime;

        if (data == null || data.isEmpty()) {
            tokens = targetCapacity;
            lastRefillTime = now;
        } else {
            Object tokensObj = data.get("tokens");
            Object lastRefillObj = data.get("lastRefillTime");

            if (tokensObj == null || lastRefillObj == null) {
                tokens = targetCapacity;
                lastRefillTime = now;
            } else {
                try {
                    tokens = Double.parseDouble(tokensObj.toString());
                    lastRefillTime = Long.parseLong(lastRefillObj.toString());
                } catch (NumberFormatException e) {
                    tokens = targetCapacity;
                    lastRefillTime = now;
                }
            }
        }

        // Refill tokens
        long timePassed = now - lastRefillTime;
        if (timePassed > 0) {
            tokens = Math.min(targetCapacity, tokens + (timePassed * refillRatePerMs));
            lastRefillTime = now;
        }

        // Consume token
        if (tokens >= 1.0) {
            tokens -= 1.0;

            Map<String, Object> update = new HashMap<>();
            update.put("tokens", tokens);
            update.put("lastRefillTime", lastRefillTime);

            redisTemplate.opsForHash().putAll(key, update);
            redisTemplate.expire(key, refillIntervalSeconds, TimeUnit.SECONDS);
            return true;
        }

        // Still update refilled tokens in Redis even if consumer request fails (so refill progress isn't lost)
        Map<String, Object> update = new HashMap<>();
        update.put("tokens", tokens);
        update.put("lastRefillTime", lastRefillTime);
        redisTemplate.opsForHash().putAll(key, update);
        redisTemplate.expire(key, refillIntervalSeconds, TimeUnit.SECONDS);

        return false;
    }

    private String getClientIp(HttpServletRequest request) {
        // When server.forward-headers-strategy=framework is enabled,
        // ForwardedHeaderFilter safely updates request.getRemoteAddr()
        // only if forwarded headers come from a trusted proxy.
        // We never blindly parse raw unverified X-Forwarded-For headers directly.
        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr != null && !remoteAddr.isBlank()) ? remoteAddr : "unknown";
    }

    private static class TokenBucket {
        private final long capacity;
        private double tokens;
        private long lastRefillTime;

        public TokenBucket(long capacity) {
            this.capacity = capacity;
            this.tokens = capacity;
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume(double refillRatePerMs) {
            refill(refillRatePerMs);
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }

        private void refill(double refillRatePerMs) {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTime;
            if (timePassed > 0) {
                tokens = Math.min(capacity, tokens + (timePassed * refillRatePerMs));
                lastRefillTime = now;
            }
        }
    }
}
