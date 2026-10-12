package com.blood_donor.repository.donor_repository;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.repository.donor_repository.projection.BloodGroupCount;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface DonorStatsRepository {
    public long countByIsAvailableTrue();

    public long countByBloodGroup(BloodGroup bloodGroup);

    @Query(value="SELECT dp.bloodGroup AS bloodGroup,\n       COUNT(dp)    AS count\nFROM DonorProfile dp\nGROUP BY dp.bloodGroup\nORDER BY COUNT(dp) DESC\n")
    public List<BloodGroupCount> countByBloodGroupGrouped();
}

