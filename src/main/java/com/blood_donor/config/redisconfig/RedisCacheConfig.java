package com.blood_donor.config.redisconfig;

import com.blood_donor.config.properties.RedisCacheProperties;
import java.time.Duration;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
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
public class RedisCacheConfig {
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
            log.info("Cache configured: {} \u00e2\u2020\u2019 {} minutes", cacheName, ttlMinutes);
        });
        return RedisCacheManager.builder((RedisConnectionFactory)factory).cacheDefaults(defaultConfig).withInitialCacheConfigurations(cacheConfigs).build();
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

