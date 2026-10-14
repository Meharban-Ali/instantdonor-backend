package com.blood_donor.config;

import com.blood_donor.common.enums.BloodGroup;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToBloodGroupConverter implements Converter<String, BloodGroup> {
    @Override
    public BloodGroup convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return BloodGroup.fromDisplayNameOrThrow(source);
    }
}
