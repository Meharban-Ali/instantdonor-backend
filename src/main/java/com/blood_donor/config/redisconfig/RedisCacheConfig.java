package com.blood_donor.config.redisconfig;

import com.blood_donor.config.properties.RedisCacheProperties;
import java.time.Duration;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile(value={"!test"})
@EnableCaching
public class RedisCacheConfig implements CachingConfigurer {
    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);
    private final RedisCacheProperties cacheProperties;
    private final Jackson2JsonRedisSerializer<Object> jsonSerializer;
    private final StringRedisSerializer stringSerializer;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration defaultConfig = this.buildCacheConfig(this.cacheProperties.getDefaultTtlMinutes());
        HashMap cacheConfigs = new HashMap();
        this.cacheProperties.getTtlMap().forEach((cacheName, ttlMinutes) -> {
            cacheConfigs.put(cacheName, this.buildCacheConfig((long)ttlMinutes));
            log.info("Cache configured: {} -> {} minutes", cacheName, ttlMinutes);
        });
        return RedisCacheManager.builder((RedisConnectionFactory)factory).cacheDefaults(defaultConfig).withInitialCacheConfigurations(cacheConfigs).build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis GET failed for cache [{}] key [{}]: {}. Fallback to DB.",
                        cache != null ? cache.getName() : "unknown", key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Redis PUT failed for cache [{}] key [{}]: {}. Ignoring write failure.",
                        cache != null ? cache.getName() : "unknown", key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis EVICT failed for cache [{}] key [{}]: {}. Ignoring evict failure.",
                        cache != null ? cache.getName() : "unknown", key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis CLEAR failed for cache [{}]: {}. Ignoring clear failure.",
                        cache != null ? cache.getName() : "unknown", exception.getMessage());
            }
        };
    }

    private RedisCacheConfiguration buildCacheConfig(long ttlMinutes) {
        return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(ttlMinutes)).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer((RedisSerializer)this.stringSerializer)).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(this.jsonSerializer)).disableCachingNullValues();
    }

    public RedisCacheConfig(RedisCacheProperties cacheProperties, Jackson2JsonRedisSerializer<Object> jsonSerializer, StringRedisSerializer stringSerializer) {
        this.cacheProperties = cacheProperties;
        this.jsonSerializer = jsonSerializer;
        this.stringSerializer = stringSerializer;
    }
}

