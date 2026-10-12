package com.blood_donor.model.dto.request.donor_request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class RequestTimeInfo {
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private LocalDateTime fulfilledAt;
    private Long minutesRemaining;

    public static RequestTimeInfoBuilder builder() {
        return new RequestTimeInfoBuilder();
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getExpiredAt() {
        return this.expiredAt;
    }

    public LocalDateTime getFulfilledAt() {
        return this.fulfilledAt;
    }

    public Long getMinutesRemaining() {
        return this.minutesRemaining;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public void setFulfilledAt(LocalDateTime fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
    }

    public void setMinutesRemaining(Long minutesRemaining) {
        this.minutesRemaining = minutesRemaining;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RequestTimeInfo)) {
            return false;
        }
        RequestTimeInfo other = (RequestTimeInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$minutesRemaining = this.getMinutesRemaining();
        Long other$minutesRemaining = other.getMinutesRemaining();
        if (this$minutesRemaining == null ? other$minutesRemaining != null : !((Object)this$minutesRemaining).equals(other$minutesRemaining)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$expiredAt = this.getExpiredAt();
        LocalDateTime other$expiredAt = other.getExpiredAt();
        if (this$expiredAt == null ? other$expiredAt != null : !((Object)this$expiredAt).equals(other$expiredAt)) {
            return false;
        }
        LocalDateTime this$fulfilledAt = this.getFulfilledAt();
        LocalDateTime other$fulfilledAt = other.getFulfilledAt();
        return !(this$fulfilledAt == null ? other$fulfilledAt != null : !((Object)this$fulfilledAt).equals(other$fulfilledAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RequestTimeInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $minutesRemaining = this.getMinutesRemaining();
        result = result * 59 + ($minutesRemaining == null ? 43 : ((Object)$minutesRemaining).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $expiredAt = this.getExpiredAt();
        result = result * 59 + ($expiredAt == null ? 43 : ((Object)$expiredAt).hashCode());
        LocalDateTime $fulfilledAt = this.getFulfilledAt();
        result = result * 59 + ($fulfilledAt == null ? 43 : ((Object)$fulfilledAt).hashCode());
        return result;
    }

    public String toString() {
        return "RequestTimeInfo(createdAt=" + this.getCreatedAt() + ", expiredAt=" + this.getExpiredAt() + ", fulfilledAt=" + this.getFulfilledAt() + ", minutesRemaining=" + this.getMinutesRemaining() + ")";
    }

    public RequestTimeInfo() {
    }

    public RequestTimeInfo(LocalDateTime createdAt, LocalDateTime expiredAt, LocalDateTime fulfilledAt, Long minutesRemaining) {
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.fulfilledAt = fulfilledAt;
        this.minutesRemaining = minutesRemaining;
    }

    public static class RequestTimeInfoBuilder {
        private LocalDateTime createdAt;
        private LocalDateTime expiredAt;
        private LocalDateTime fulfilledAt;
        private Long minutesRemaining;

        RequestTimeInfoBuilder() {
        }

        public RequestTimeInfoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RequestTimeInfoBuilder expiredAt(LocalDateTime expiredAt) {
            this.expiredAt = expiredAt;
            return this;
        }

        public RequestTimeInfoBuilder fulfilledAt(LocalDateTime fulfilledAt) {
            this.fulfilledAt = fulfilledAt;
            return this;
        }

        public RequestTimeInfoBuilder minutesRemaining(Long minutesRemaining) {
            this.minutesRemaining = minutesRemaining;
            return this;
        }

        public RequestTimeInfo build() {
            return new RequestTimeInfo(this.createdAt, this.expiredAt, this.fulfilledAt, this.minutesRemaining);
        }

        public String toString() {
            return "RequestTimeInfo.RequestTimeInfoBuilder(createdAt=" + this.createdAt + ", expiredAt=" + this.expiredAt + ", fulfilledAt=" + this.fulfilledAt + ", minutesRemaining=" + this.minutesRemaining + ")";
        }
    }
}

