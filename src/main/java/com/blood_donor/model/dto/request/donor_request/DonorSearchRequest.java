package com.blood_donor.model.dto.request.donor_request;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.dto.request.donor_request.PaginationRequest;
import com.blood_donor.model.dto.request.donor_request.RadiusSearchRequest;
import com.blood_donor.model.dto.request.donor_request.validation.CityOrRadius;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@CityOrRadius
public class DonorSearchRequest {
    @NotNull(message="Blood group is required")
    private @NotNull(message="Blood group is required") BloodGroup bloodGroup;
    @Size(max=100, message="City max 100 characters")
    private @Size(max=100, message="City max 100 characters") String city;
    @Valid
    private RadiusSearchRequest radiusSearch;
    @Valid
    private PaginationRequest pagination = new PaginationRequest();

    public boolean isCitySearch() {
        return this.city != null && !this.city.isBlank();
    }

    public boolean isRadiusSearch() {
        return this.radiusSearch != null && this.radiusSearch.getLatitude() != null && this.radiusSearch.getLongitude() != null;
    }

    public int getPage() {
        return this.pagination.getPage();
    }

    public int getSize() {
        return this.pagination.getSize();
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public String getCity() {
        return this.city;
    }

    public RadiusSearchRequest getRadiusSearch() {
        return this.radiusSearch;
    }

    public PaginationRequest getPagination() {
        return this.pagination;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setRadiusSearch(RadiusSearchRequest radiusSearch) {
        this.radiusSearch = radiusSearch;
    }

    public void setPagination(PaginationRequest pagination) {
        this.pagination = pagination;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DonorSearchRequest)) {
            return false;
        }
        DonorSearchRequest other = (DonorSearchRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        BloodGroup this$bloodGroup = this.getBloodGroup();
        BloodGroup other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !((Object)((Object)this$bloodGroup)).equals((Object)other$bloodGroup)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        RadiusSearchRequest this$radiusSearch = this.getRadiusSearch();
        RadiusSearchRequest other$radiusSearch = other.getRadiusSearch();
        if (this$radiusSearch == null ? other$radiusSearch != null : !((Object)this$radiusSearch).equals(other$radiusSearch)) {
            return false;
        }
        PaginationRequest this$pagination = this.getPagination();
        PaginationRequest other$pagination = other.getPagination();
        return !(this$pagination == null ? other$pagination != null : !((Object)this$pagination).equals(other$pagination));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DonorSearchRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        RadiusSearchRequest $radiusSearch = this.getRadiusSearch();
        result = result * 59 + ($radiusSearch == null ? 43 : ((Object)$radiusSearch).hashCode());
        PaginationRequest $pagination = this.getPagination();
        result = result * 59 + ($pagination == null ? 43 : ((Object)$pagination).hashCode());
        return result;
    }

    public String toString() {
        return "DonorSearchRequest(bloodGroup=" + this.getBloodGroup() + ", city=" + this.getCity() + ", radiusSearch=" + this.getRadiusSearch() + ", pagination=" + this.getPagination() + ")";
    }
}

