package com.blood_donor.repository.donor_repository;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.entity.DonorProfile;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonorSearchRepository {
    public Page<DonorProfile> findByBloodGroupAndCityIgnoreCaseAndIsAvailableTrue(BloodGroup var1, String var2, Pageable var3);

    public Page<DonorProfile> findByBloodGroupInAndCityIgnoreCaseAndIsAvailableTrue(List<BloodGroup> var1, String var2, Pageable var3);

    @Query(value="SELECT dp.*,\n       (6371 * acos(\n           LEAST(1.0,\n               cos(radians(:lat)) * cos(radians(dp.latitude)) *\n               cos(radians(dp.longitude) - radians(:lng)) +\n               sin(radians(:lat)) * sin(radians(dp.latitude))\n           )\n       )) AS distance_km\nFROM donor_profiles dp\nINNER JOIN users u ON dp.user_id = u.id\nWHERE dp.is_available = true\n  AND u.is_active    = true\n  AND dp.blood_group IN :bloodGroups\n  AND (6371 * acos(\n           LEAST(1.0,\n               cos(radians(:lat)) * cos(radians(dp.latitude)) *\n               cos(radians(dp.longitude) - radians(:lng)) +\n               sin(radians(:lat)) * sin(radians(dp.latitude))\n           )\n       )) <= :radiusKm\nORDER BY distance_km ASC, dp.reliability_score DESC\nLIMIT :limitCount\n", nativeQuery=true)
    public List<DonorProfile> findNearbyCompatibleDonors(@Param(value="lat") double var1, @Param(value="lng") double var3, @Param(value="radiusKm") double var5, @Param(value="bloodGroups") List<String> var7, @Param(value="limitCount") int var8);

    @Query(value="SELECT (6371 * acos(\n    LEAST(1.0,\n        cos(radians(:lat1)) * cos(radians(:lat2)) *\n        cos(radians(:lng2) - radians(:lng1)) +\n        sin(radians(:lat1)) * sin(radians(:lat2))\n    )\n)) FROM DUAL\n", nativeQuery=true)
    public Double calculateDistanceKm(@Param(value="lat1") double lat1, @Param(value="lng1") double lng1, @Param(value="lat2") double lat2, @Param(value="lng2") double lng2);
}

