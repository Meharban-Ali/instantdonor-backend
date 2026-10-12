package com.blood_donor.repository.donor_repository.projection;

public interface CityStatsProjection {
    public String getCity();

    public Long getTotalRequests();

    public Long getActiveRequests();

    public Long getFulfilledRequests();

    public Long getCriticalRequests();
}

