/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.NotNull
 */
package com.blood_donor.model.dto.request.donor_request;

import jakarta.validation.constraints.NotNull;

public class RespondToRequestDto {
    @NotNull(message="Request ID is required")
    private @NotNull(message="Request ID is required") Long requestId;
    @NotNull(message="Accept/Reject decision is required")
    private @NotNull(message="Accept/Reject decision is required") Boolean accept;
    private String notes;

    public Long getRequestId() {
        return this.requestId;
    }

    public Boolean getAccept() {
        return this.accept;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public void setAccept(Boolean accept) {
        this.accept = accept;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RespondToRequestDto)) {
            return false;
        }
        RespondToRequestDto other = (RespondToRequestDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$requestId = this.getRequestId();
        Long other$requestId = other.getRequestId();
        if (this$requestId == null ? other$requestId != null : !((Object)this$requestId).equals(other$requestId)) {
            return false;
        }
        Boolean this$accept = this.getAccept();
        Boolean other$accept = other.getAccept();
        if (this$accept == null ? other$accept != null : !((Object)this$accept).equals(other$accept)) {
            return false;
        }
        String this$notes = this.getNotes();
        String other$notes = other.getNotes();
        return !(this$notes == null ? other$notes != null : !this$notes.equals(other$notes));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RespondToRequestDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : ((Object)$requestId).hashCode());
        Boolean $accept = this.getAccept();
        result = result * 59 + ($accept == null ? 43 : ((Object)$accept).hashCode());
        String $notes = this.getNotes();
        result = result * 59 + ($notes == null ? 43 : $notes.hashCode());
        return result;
    }

    public String toString() {
        return "RespondToRequestDto(requestId=" + this.getRequestId() + ", accept=" + this.getAccept() + ", notes=" + this.getNotes() + ")";
    }
}

