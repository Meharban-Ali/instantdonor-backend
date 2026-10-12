package com.blood_donor.repository.donor_repository.projection;

public interface ResponseRateStats {
    public Long getDonorProfileId();

    public Long getTotalResponses();

    public Long getAcceptedResponses();

    public Long getRejectedResponses();

    public Long getNoResponseCount();
}

