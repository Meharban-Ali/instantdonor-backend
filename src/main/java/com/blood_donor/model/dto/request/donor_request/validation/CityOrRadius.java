/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.Constraint
 *  jakarta.validation.Payload
 */
package com.blood_donor.model.dto.request.donor_request.validation;

import com.blood_donor.model.dto.request.donor_request.validation.CityOrRadiusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Constraint(validatedBy={CityOrRadiusValidator.class})
public @interface CityOrRadius {
    public String message() default "Either city or coordinates (latitude + longitude) must be provided";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}

