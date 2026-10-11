/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Profile
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
 *  org.springframework.data.redis.serializer.RedisSerializer
 *  org.springframework.data.redis.serializer.StringRedisSerializer
 */
package com.blood_donor.config.redisconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile(value={"!test"})
public class RedisTemplateConfig {
    private final Jackson2JsonRedisSerializer<Object> jsonSerializer;
    private final StringRedisSerializer stringSerializer;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(factory);
        template.setKeySerializer((RedisSerializer)this.stringSerializer);
        template.setHashKeySerializer((RedisSerializer)this.stringSerializer);
        template.setValueSerializer(this.jsonSerializer);
        template.setHashValueSerializer(this.jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    public RedisTemplateConfig(Jackson2JsonRedisSerializer<Object> jsonSerializer, StringRedisSerializer stringSerializer) {
        this.jsonSerializer = jsonSerializer;
        this.stringSerializer = stringSerializer;
    }
}

