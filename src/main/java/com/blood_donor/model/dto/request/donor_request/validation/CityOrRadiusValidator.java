package com.blood_donor.model.dto.request.donor_request.validation;

import com.blood_donor.model.dto.request.donor_request.DonorSearchRequest;
import com.blood_donor.model.dto.request.donor_request.validation.CityOrRadius;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CityOrRadiusValidator
implements ConstraintValidator<CityOrRadius, DonorSearchRequest> {
    public boolean isValid(DonorSearchRequest request, ConstraintValidatorContext context) {
        boolean hasCoordinates;
        if (request == null) {
            return true;
        }
        boolean hasCity = request.getCity() != null && !request.getCity().isBlank();
        boolean bl = hasCoordinates = request.getRadiusSearch() != null && request.getRadiusSearch().getLatitude() != null && request.getRadiusSearch().getLongitude() != null;
        if (!hasCity && !hasCoordinates) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Either 'city' or 'radiusSearch' (with latitude & longitude) is required").addConstraintViolation();
            return false;
        }
        return true;
    }
}

