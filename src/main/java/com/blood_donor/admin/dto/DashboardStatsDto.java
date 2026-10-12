package com.blood_donor.admin.dto;

import com.blood_donor.admin.dto.BloodGroupStatsDto;
import com.blood_donor.admin.dto.CityDemandDto;
import com.blood_donor.admin.dto.PlatformHealthDto;
import com.blood_donor.admin.dto.RecentRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DashboardStatsDto {
    private long totalUsers;
    private long activeUsers;
    private long totalDonors;
    private long activeDonors;
    private long totalHospitals;
    private long newUsersThisMonth;
    private long totalRequests;
    private long activeRequests;
    private long fulfilledRequests;
    private long expiredRequests;
    private long cancelledRequests;
    private long criticalRequests;
    private long requestsToday;
    private long totalDonations;
    private long donationsThisMonth;
    private long donationsThisYear;
    private long donationsToday;
    private List<BloodGroupStatsDto> bloodGroupStats;
    private List<CityDemandDto> topCitiesByDemand;
    private List<RecentRequestDto> recentRequests;
    private PlatformHealthDto platformHealth;
    private LocalDateTime generatedAt;
    private String dataSource;

    public Double getFulfillmentRate() {
        return this.platformHealth != null ? this.platformHealth.getFulfillmentRate() : null;
    }

    public Double getAvgResponseTime() {
        return this.platformHealth != null ? this.platformHealth.getAvgResponseTimeHours() : null;
    }

    public boolean hasCriticalEmergency() {
        return this.criticalRequests > 0L;
    }

    public boolean hasShortage() {
        if (this.bloodGroupStats == null) {
            return false;
        }
        return this.bloodGroupStats.stream().anyMatch(BloodGroupStatsDto::isInShortage);
    }

    public static DashboardStatsDtoBuilder builder() {
        return new DashboardStatsDtoBuilder();
    }

    public long getTotalUsers() {
        return this.totalUsers;
    }

    public long getActiveUsers() {
        return this.activeUsers;
    }

    public long getTotalDonors() {
        return this.totalDonors;
    }

    public long getActiveDonors() {
        return this.activeDonors;
    }

    public long getTotalHospitals() {
        return this.totalHospitals;
    }

    public long getNewUsersThisMonth() {
        return this.newUsersThisMonth;
    }

    public long getTotalRequests() {
        return this.totalRequests;
    }

    public long getActiveRequests() {
        return this.activeRequests;
    }

    public long getFulfilledRequests() {
        return this.fulfilledRequests;
    }

    public long getExpiredRequests() {
        return this.expiredRequests;
    }

    public long getCancelledRequests() {
        return this.cancelledRequests;
    }

    public long getCriticalRequests() {
        return this.criticalRequests;
    }

    public long getRequestsToday() {
        return this.requestsToday;
    }

    public long getTotalDonations() {
        return this.totalDonations;
    }

    public long getDonationsThisMonth() {
        return this.donationsThisMonth;
    }

    public long getDonationsThisYear() {
        return this.donationsThisYear;
    }

    public long getDonationsToday() {
        return this.donationsToday;
    }

    public List<BloodGroupStatsDto> getBloodGroupStats() {
        return this.bloodGroupStats;
    }

    public List<CityDemandDto> getTopCitiesByDemand() {
        return this.topCitiesByDemand;
    }

    public List<RecentRequestDto> getRecentRequests() {
        return this.recentRequests;
    }

    public PlatformHealthDto getPlatformHealth() {
        return this.platformHealth;
    }

    public LocalDateTime getGeneratedAt() {
        return this.generatedAt;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public void setTotalDonors(long totalDonors) {
        this.totalDonors = totalDonors;
    }

    public void setActiveDonors(long activeDonors) {
        this.activeDonors = activeDonors;
    }

    public void setTotalHospitals(long totalHospitals) {
        this.totalHospitals = totalHospitals;
    }

    public void setNewUsersThisMonth(long newUsersThisMonth) {
        this.newUsersThisMonth = newUsersThisMonth;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public void setActiveRequests(long activeRequests) {
        this.activeRequests = activeRequests;
    }

    public void setFulfilledRequests(long fulfilledRequests) {
        this.fulfilledRequests = fulfilledRequests;
    }

    public void setExpiredRequests(long expiredRequests) {
        this.expiredRequests = expiredRequests;
    }

    public void setCancelledRequests(long cancelledRequests) {
        this.cancelledRequests = cancelledRequests;
    }

    public void setCriticalRequests(long criticalRequests) {
        this.criticalRequests = criticalRequests;
    }

    public void setRequestsToday(long requestsToday) {
        this.requestsToday = requestsToday;
    }

    public void setTotalDonations(long totalDonations) {
        this.totalDonations = totalDonations;
    }

    public void setDonationsThisMonth(long donationsThisMonth) {
        this.donationsThisMonth = donationsThisMonth;
    }

    public void setDonationsThisYear(long donationsThisYear) {
        this.donationsThisYear = donationsThisYear;
    }

    public void setDonationsToday(long donationsToday) {
        this.donationsToday = donationsToday;
    }

    public void setBloodGroupStats(List<BloodGroupStatsDto> bloodGroupStats) {
        this.bloodGroupStats = bloodGroupStats;
    }

    public void setTopCitiesByDemand(List<CityDemandDto> topCitiesByDemand) {
        this.topCitiesByDemand = topCitiesByDemand;
    }

    public void setRecentRequests(List<RecentRequestDto> recentRequests) {
        this.recentRequests = recentRequests;
    }

    public void setPlatformHealth(PlatformHealthDto platformHealth) {
        this.platformHealth = platformHealth;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DashboardStatsDto)) {
            return false;
        }
        DashboardStatsDto other = (DashboardStatsDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getTotalUsers() != other.getTotalUsers()) {
            return false;
        }
        if (this.getActiveUsers() != other.getActiveUsers()) {
            return false;
        }
        if (this.getTotalDonors() != other.getTotalDonors()) {
            return false;
        }
        if (this.getActiveDonors() != other.getActiveDonors()) {
            return false;
        }
        if (this.getTotalHospitals() != other.getTotalHospitals()) {
            return false;
        }
        if (this.getNewUsersThisMonth() != other.getNewUsersThisMonth()) {
            return false;
        }
        if (this.getTotalRequests() != other.getTotalRequests()) {
            return false;
        }
        if (this.getActiveRequests() != other.getActiveRequests()) {
            return false;
        }
        if (this.getFulfilledRequests() != other.getFulfilledRequests()) {
            return false;
        }
        if (this.getExpiredRequests() != other.getExpiredRequests()) {
            return false;
        }
        if (this.getCancelledRequests() != other.getCancelledRequests()) {
            return false;
        }
        if (this.getCriticalRequests() != other.getCriticalRequests()) {
            return false;
        }
        if (this.getRequestsToday() != other.getRequestsToday()) {
            return false;
        }
        if (this.getTotalDonations() != other.getTotalDonations()) {
            return false;
        }
        if (this.getDonationsThisMonth() != other.getDonationsThisMonth()) {
            return false;
        }
        if (this.getDonationsThisYear() != other.getDonationsThisYear()) {
            return false;
        }
        if (this.getDonationsToday() != other.getDonationsToday()) {
            return false;
        }
        List<BloodGroupStatsDto> this$bloodGroupStats = this.getBloodGroupStats();
        List<BloodGroupStatsDto> other$bloodGroupStats = other.getBloodGroupStats();
        if (this$bloodGroupStats == null ? other$bloodGroupStats != null : !((Object)this$bloodGroupStats).equals(other$bloodGroupStats)) {
            return false;
        }
        List<CityDemandDto> this$topCitiesByDemand = this.getTopCitiesByDemand();
        List<CityDemandDto> other$topCitiesByDemand = other.getTopCitiesByDemand();
        if (this$topCitiesByDemand == null ? other$topCitiesByDemand != null : !((Object)this$topCitiesByDemand).equals(other$topCitiesByDemand)) {
            return false;
        }
        List<RecentRequestDto> this$recentRequests = this.getRecentRequests();
        List<RecentRequestDto> other$recentRequests = other.getRecentRequests();
        if (this$recentRequests == null ? other$recentRequests != null : !((Object)this$recentRequests).equals(other$recentRequests)) {
            return false;
        }
        PlatformHealthDto this$platformHealth = this.getPlatformHealth();
        PlatformHealthDto other$platformHealth = other.getPlatformHealth();
        if (this$platformHealth == null ? other$platformHealth != null : !((Object)this$platformHealth).equals(other$platformHealth)) {
            return false;
        }
        LocalDateTime this$generatedAt = this.getGeneratedAt();
        LocalDateTime other$generatedAt = other.getGeneratedAt();
        if (this$generatedAt == null ? other$generatedAt != null : !((Object)this$generatedAt).equals(other$generatedAt)) {
            return false;
        }
        String this$dataSource = this.getDataSource();
        String other$dataSource = other.getDataSource();
        return !(this$dataSource == null ? other$dataSource != null : !this$dataSource.equals(other$dataSource));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DashboardStatsDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $totalUsers = this.getTotalUsers();
        result = result * 59 + (int)($totalUsers >>> 32 ^ $totalUsers);
        long $activeUsers = this.getActiveUsers();
        result = result * 59 + (int)($activeUsers >>> 32 ^ $activeUsers);
        long $totalDonors = this.getTotalDonors();
        result = result * 59 + (int)($totalDonors >>> 32 ^ $totalDonors);
        long $activeDonors = this.getActiveDonors();
        result = result * 59 + (int)($activeDonors >>> 32 ^ $activeDonors);
        long $totalHospitals = this.getTotalHospitals();
        result = result * 59 + (int)($totalHospitals >>> 32 ^ $totalHospitals);
        long $newUsersThisMonth = this.getNewUsersThisMonth();
        result = result * 59 + (int)($newUsersThisMonth >>> 32 ^ $newUsersThisMonth);
        long $totalRequests = this.getTotalRequests();
        result = result * 59 + (int)($totalRequests >>> 32 ^ $totalRequests);
        long $activeRequests = this.getActiveRequests();
        result = result * 59 + (int)($activeRequests >>> 32 ^ $activeRequests);
        long $fulfilledRequests = this.getFulfilledRequests();
        result = result * 59 + (int)($fulfilledRequests >>> 32 ^ $fulfilledRequests);
        long $expiredRequests = this.getExpiredRequests();
        result = result * 59 + (int)($expiredRequests >>> 32 ^ $expiredRequests);
        long $cancelledRequests = this.getCancelledRequests();
        result = result * 59 + (int)($cancelledRequests >>> 32 ^ $cancelledRequests);
        long $criticalRequests = this.getCriticalRequests();
        result = result * 59 + (int)($criticalRequests >>> 32 ^ $criticalRequests);
        long $requestsToday = this.getRequestsToday();
        result = result * 59 + (int)($requestsToday >>> 32 ^ $requestsToday);
        long $totalDonations = this.getTotalDonations();
        result = result * 59 + (int)($totalDonations >>> 32 ^ $totalDonations);
        long $donationsThisMonth = this.getDonationsThisMonth();
        result = result * 59 + (int)($donationsThisMonth >>> 32 ^ $donationsThisMonth);
        long $donationsThisYear = this.getDonationsThisYear();
        result = result * 59 + (int)($donationsThisYear >>> 32 ^ $donationsThisYear);
        long $donationsToday = this.getDonationsToday();
        result = result * 59 + (int)($donationsToday >>> 32 ^ $donationsToday);
        List<BloodGroupStatsDto> $bloodGroupStats = this.getBloodGroupStats();
        result = result * 59 + ($bloodGroupStats == null ? 43 : ((Object)$bloodGroupStats).hashCode());
        List<CityDemandDto> $topCitiesByDemand = this.getTopCitiesByDemand();
        result = result * 59 + ($topCitiesByDemand == null ? 43 : ((Object)$topCitiesByDemand).hashCode());
        List<RecentRequestDto> $recentRequests = this.getRecentRequests();
        result = result * 59 + ($recentRequests == null ? 43 : ((Object)$recentRequests).hashCode());
        PlatformHealthDto $platformHealth = this.getPlatformHealth();
        result = result * 59 + ($platformHealth == null ? 43 : ((Object)$platformHealth).hashCode());
        LocalDateTime $generatedAt = this.getGeneratedAt();
        result = result * 59 + ($generatedAt == null ? 43 : ((Object)$generatedAt).hashCode());
        String $dataSource = this.getDataSource();
        result = result * 59 + ($dataSource == null ? 43 : $dataSource.hashCode());
        return result;
    }

    public String toString() {
        return "DashboardStatsDto(totalUsers=" + this.getTotalUsers() + ", activeUsers=" + this.getActiveUsers() + ", totalDonors=" + this.getTotalDonors() + ", activeDonors=" + this.getActiveDonors() + ", totalHospitals=" + this.getTotalHospitals() + ", newUsersThisMonth=" + this.getNewUsersThisMonth() + ", totalRequests=" + this.getTotalRequests() + ", activeRequests=" + this.getActiveRequests() + ", fulfilledRequests=" + this.getFulfilledRequests() + ", expiredRequests=" + this.getExpiredRequests() + ", cancelledRequests=" + this.getCancelledRequests() + ", criticalRequests=" + this.getCriticalRequests() + ", requestsToday=" + this.getRequestsToday() + ", totalDonations=" + this.getTotalDonations() + ", donationsThisMonth=" + this.getDonationsThisMonth() + ", donationsThisYear=" + this.getDonationsThisYear() + ", donationsToday=" + this.getDonationsToday() + ", bloodGroupStats=" + this.getBloodGroupStats() + ", topCitiesByDemand=" + this.getTopCitiesByDemand() + ", recentRequests=" + this.getRecentRequests() + ", platformHealth=" + this.getPlatformHealth() + ", generatedAt=" + this.getGeneratedAt() + ", dataSource=" + this.getDataSource() + ")";
    }

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalUsers, long activeUsers, long totalDonors, long activeDonors, long totalHospitals, long newUsersThisMonth, long totalRequests, long activeRequests, long fulfilledRequests, long expiredRequests, long cancelledRequests, long criticalRequests, long requestsToday, long totalDonations, long donationsThisMonth, long donationsThisYear, long donationsToday, List<BloodGroupStatsDto> bloodGroupStats, List<CityDemandDto> topCitiesByDemand, List<RecentRequestDto> recentRequests, PlatformHealthDto platformHealth, LocalDateTime generatedAt, String dataSource) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.totalDonors = totalDonors;
        this.activeDonors = activeDonors;
        this.totalHospitals = totalHospitals;
        this.newUsersThisMonth = newUsersThisMonth;
        this.totalRequests = totalRequests;
        this.activeRequests = activeRequests;
        this.fulfilledRequests = fulfilledRequests;
        this.expiredRequests = expiredRequests;
        this.cancelledRequests = cancelledRequests;
        this.criticalRequests = criticalRequests;
        this.requestsToday = requestsToday;
        this.totalDonations = totalDonations;
        this.donationsThisMonth = donationsThisMonth;
        this.donationsThisYear = donationsThisYear;
        this.donationsToday = donationsToday;
        this.bloodGroupStats = bloodGroupStats;
        this.topCitiesByDemand = topCitiesByDemand;
        this.recentRequests = recentRequests;
        this.platformHealth = platformHealth;
        this.generatedAt = generatedAt;
        this.dataSource = dataSource;
    }

    public static class DashboardStatsDtoBuilder {
        private long totalUsers;
        private long activeUsers;
        private long totalDonors;
        private long activeDonors;
        private long totalHospitals;
        private long newUsersThisMonth;
        private long totalRequests;
        private long activeRequests;
        private long fulfilledRequests;
        private long expiredRequests;
        private long cancelledRequests;
        private long criticalRequests;
        private long requestsToday;
        private long totalDonations;
        private long donationsThisMonth;
        private long donationsThisYear;
        private long donationsToday;
        private List<BloodGroupStatsDto> bloodGroupStats;
        private List<CityDemandDto> topCitiesByDemand;
        private List<RecentRequestDto> recentRequests;
        private PlatformHealthDto platformHealth;
        private LocalDateTime generatedAt;
        private String dataSource;

        DashboardStatsDtoBuilder() {
        }

        public DashboardStatsDtoBuilder totalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
            return this;
        }

        public DashboardStatsDtoBuilder activeUsers(long activeUsers) {
            this.activeUsers = activeUsers;
            return this;
        }

        public DashboardStatsDtoBuilder totalDonors(long totalDonors) {
            this.totalDonors = totalDonors;
            return this;
        }

        public DashboardStatsDtoBuilder activeDonors(long activeDonors) {
            this.activeDonors = activeDonors;
            return this;
        }

        public DashboardStatsDtoBuilder totalHospitals(long totalHospitals) {
            this.totalHospitals = totalHospitals;
            return this;
        }

        public DashboardStatsDtoBuilder newUsersThisMonth(long newUsersThisMonth) {
            this.newUsersThisMonth = newUsersThisMonth;
            return this;
        }

        public DashboardStatsDtoBuilder totalRequests(long totalRequests) {
            this.totalRequests = totalRequests;
            return this;
        }

        public DashboardStatsDtoBuilder activeRequests(long activeRequests) {
            this.activeRequests = activeRequests;
            return this;
        }

        public DashboardStatsDtoBuilder fulfilledRequests(long fulfilledRequests) {
            this.fulfilledRequests = fulfilledRequests;
            return this;
        }

        public DashboardStatsDtoBuilder expiredRequests(long expiredRequests) {
            this.expiredRequests = expiredRequests;
            return this;
        }

        public DashboardStatsDtoBuilder cancelledRequests(long cancelledRequests) {
            this.cancelledRequests = cancelledRequests;
            return this;
        }

        public DashboardStatsDtoBuilder criticalRequests(long criticalRequests) {
            this.criticalRequests = criticalRequests;
            return this;
        }

        public DashboardStatsDtoBuilder requestsToday(long requestsToday) {
            this.requestsToday = requestsToday;
            return this;
        }

        public DashboardStatsDtoBuilder totalDonations(long totalDonations) {
            this.totalDonations = totalDonations;
            return this;
        }

        public DashboardStatsDtoBuilder donationsThisMonth(long donationsThisMonth) {
            this.donationsThisMonth = donationsThisMonth;
            return this;
        }

        public DashboardStatsDtoBuilder donationsThisYear(long donationsThisYear) {
            this.donationsThisYear = donationsThisYear;
            return this;
        }

        public DashboardStatsDtoBuilder donationsToday(long donationsToday) {
            this.donationsToday = donationsToday;
            return this;
        }

        public DashboardStatsDtoBuilder bloodGroupStats(List<BloodGroupStatsDto> bloodGroupStats) {
            this.bloodGroupStats = bloodGroupStats;
            return this;
        }

        public DashboardStatsDtoBuilder topCitiesByDemand(List<CityDemandDto> topCitiesByDemand) {
            this.topCitiesByDemand = topCitiesByDemand;
            return this;
        }

        public DashboardStatsDtoBuilder recentRequests(List<RecentRequestDto> recentRequests) {
            this.recentRequests = recentRequests;
            return this;
        }

        public DashboardStatsDtoBuilder platformHealth(PlatformHealthDto platformHealth) {
            this.platformHealth = platformHealth;
            return this;
        }

        public DashboardStatsDtoBuilder generatedAt(LocalDateTime generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }

        public DashboardStatsDtoBuilder dataSource(String dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public DashboardStatsDto build() {
            return new DashboardStatsDto(this.totalUsers, this.activeUsers, this.totalDonors, this.activeDonors, this.totalHospitals, this.newUsersThisMonth, this.totalRequests, this.activeRequests, this.fulfilledRequests, this.expiredRequests, this.cancelledRequests, this.criticalRequests, this.requestsToday, this.totalDonations, this.donationsThisMonth, this.donationsThisYear, this.donationsToday, this.bloodGroupStats, this.topCitiesByDemand, this.recentRequests, this.platformHealth, this.generatedAt, this.dataSource);
        }

        public String toString() {
            return "DashboardStatsDto.DashboardStatsDtoBuilder(totalUsers=" + this.totalUsers + ", activeUsers=" + this.activeUsers + ", totalDonors=" + this.totalDonors + ", activeDonors=" + this.activeDonors + ", totalHospitals=" + this.totalHospitals + ", newUsersThisMonth=" + this.newUsersThisMonth + ", totalRequests=" + this.totalRequests + ", activeRequests=" + this.activeRequests + ", fulfilledRequests=" + this.fulfilledRequests + ", expiredRequests=" + this.expiredRequests + ", cancelledRequests=" + this.cancelledRequests + ", criticalRequests=" + this.criticalRequests + ", requestsToday=" + this.requestsToday + ", totalDonations=" + this.totalDonations + ", donationsThisMonth=" + this.donationsThisMonth + ", donationsThisYear=" + this.donationsThisYear + ", donationsToday=" + this.donationsToday + ", bloodGroupStats=" + this.bloodGroupStats + ", topCitiesByDemand=" + this.topCitiesByDemand + ", recentRequests=" + this.recentRequests + ", platformHealth=" + this.platformHealth + ", generatedAt=" + this.generatedAt + ", dataSource=" + this.dataSource + ")";
        }
    }
}

