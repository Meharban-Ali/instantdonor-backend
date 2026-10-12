package com.blood_donor.model.dto.request.donor_request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class PaginationRequest {
    @Min(value=0L, message="Page number must be >= 0")
    private @Min(value=0L, message="Page number must be >= 0") int page = 0;
    @Min(value=1L, message="Page size must be >= 1")
    @Max(value=50L, message="Page size must be <= 50")
    private @Min(value=1L, message="Page size must be >= 1") @Max(value=50L, message="Page size must be <= 50") int size = 10;

    public int getPage() {
        return this.page;
    }

    public int getSize() {
        return this.size;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PaginationRequest)) {
            return false;
        }
        PaginationRequest other = (PaginationRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getPage() != other.getPage()) {
            return false;
        }
        return this.getSize() == other.getSize();
    }

    protected boolean canEqual(Object other) {
        return other instanceof PaginationRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPage();
        result = result * 59 + this.getSize();
        return result;
    }

    public String toString() {
        return "PaginationRequest(page=" + this.getPage() + ", size=" + this.getSize() + ")";
    }
}

