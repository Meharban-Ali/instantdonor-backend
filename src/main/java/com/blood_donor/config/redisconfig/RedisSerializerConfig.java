/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$As
 *  com.fasterxml.jackson.databind.Module
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.ObjectMapper$DefaultTyping
 *  com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
 *  com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
 *  com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
 *  org.springframework.data.redis.serializer.StringRedisSerializer
 */
package com.blood_donor.config.redisconfig;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisSerializerConfig {
    private ObjectMapper createRedisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule((Module)new JavaTimeModule());
        mapper.activateDefaultTyping((PolymorphicTypeValidator)BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return mapper;
    }

    @Bean
    public Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        return new Jackson2JsonRedisSerializer(this.createRedisObjectMapper(), Object.class);
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }
}

