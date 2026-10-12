package com.blood_donor.repository.donor_repository.response_repository;

import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.repository.donor_repository.projection.ResponseRateStats;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonorResponseStatsRepository {
    public long countByDonorProfileId(Long var1);

    public long countByDonorProfileIdAndStatus(Long var1, ResponseStatus var2);

    public long countByRequestIdAndStatus(Long var1, ResponseStatus var2);

    @Query(value="SELECT\n    drr.donor.id        AS donorProfileId,\n    COUNT(drr)          AS totalResponses,\n    SUM(CASE WHEN drr.status = 'ACCEPTED'    THEN 1 ELSE 0 END)\n                        AS acceptedResponses,\n    SUM(CASE WHEN drr.status = 'REJECTED'    THEN 1 ELSE 0 END)\n                        AS rejectedResponses,\n    SUM(CASE WHEN drr.status = 'NO_RESPONSE' THEN 1 ELSE 0 END)\n                        AS noResponseCount\nFROM DonorRequestResponse drr\nWHERE drr.donor.id = :donorProfileId\nGROUP BY drr.donor.id\n")
    public Optional<ResponseRateStats> getDonorResponseStats(@Param(value="donorProfileId") Long var1);

    @Query(value="SELECT\n    CASE\n        WHEN COUNT(drr) = 0 THEN 0.0\n        ELSE (SUM(CASE WHEN drr.status = 'ACCEPTED' THEN 1\n                      ELSE 0 END) * 100.0) / COUNT(drr)\n    END\nFROM DonorRequestResponse drr\nWHERE drr.donor.id = :donorProfileId\n")
    public Double calculateResponseRate(@Param(value="donorProfileId") Long var1);
}

