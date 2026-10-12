#!/usr/bin/env python3
"""
scripts/clean_decompiler_artifacts.py

Scans all Java source files under src/main/java and cleans:
1. CFR decompiler headers (/* Decompiled with CFR ... */)
2. Synthetic CFR comments (// empty catch block, etc.)
3. Decompiled parameter names (var1, var2...) in interfaces where matching method signatures exist.
"""

import os
import re

SRC_DIR = "src/main/java"

CFR_HEADER_PATTERN = re.compile(
    r"/\*[\s\S]*?Decompiled with CFR[\s\S]*?\*/\s*",
    re.MULTILINE
)

EMPTY_CATCH_PATTERN = re.compile(r"//\s*empty catch block\s*\n")

INTERFACE_PARAM_REPLACEMENTS = {
    "getAllDonors(String var1, BloodGroup var2, int var3, int var4)": "getAllDonors(String search, BloodGroup bloodGroup, int page, int size)",
    "resetDonorScore(Long var1)": "resetDonorScore(Long donorId)",
    "verifyDonor(Long var1)": "verifyDonor(Long donorId)",
    "getDonorById(Long var1)": "getDonorById(Long donorId)",
    "getAllUsers(Role var1, String var2, int var3, int var4)": "getAllUsers(Role role, String search, int page, int size)",
    "toggleUserStatus(Long var1)": "toggleUserStatus(Long userId)",
    "updateUserRole(Long var1, Role var2)": "updateUserRole(Long userId, Role role)",
    "getUserById(Long var1)": "getUserById(Long userId)",
    "findBestDonors(AiMatchRequest var1)": "findBestDonors(AiMatchRequest request)",
    "predictDemand(AiDemandRequest var1)": "predictDemand(AiDemandRequest request)",
    "findDonors(BloodRequest var1, int var2)": "findDonors(BloodRequest request, int limit)",
    "sendHtmlEmail(String var1, String var2, String var3, Map<String, Object> var4)": "sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel)",
    "sendSimpleEmail(String var1, String var2, String var3)": "sendSimpleEmail(String to, String subject, String text)",
    "sendBulkHtmlEmail(List<String> var1, String var2, String var3, Map<String, Object> var4)": "sendBulkHtmlEmail(List<String> recipients, String subject, String templateName, Map<String, Object> templateModel)",
    "sendEmergencyAlert(BloodRequest var1)": "sendEmergencyAlert(BloodRequest request)",
    "sendDonorAcceptedConfirmation(DonorProfile var1, BloodRequest var2)": "sendDonorAcceptedConfirmation(DonorProfile donor, BloodRequest request)",
    "sendRequestFulfilledNotification(BloodRequest var1)": "sendRequestFulfilledNotification(BloodRequest request)",
    "sendPasswordResetEmail(String var1, String var2, String var3)": "sendPasswordResetEmail(String to, String name, String resetToken)",
    "sendSms(String var1, String var2)": "sendSms(String to, String message)",
    "sendBulkSms(List<String> var1, String var2)": "sendBulkSms(List<String> recipients, String message)",
    "findByUserId(Long var1)": "findByUserId(Long userId)",
    "existsByUserId(Long var1)": "existsByUserId(Long userId)",
    "findByUserIdIn(List<Long> var1)": "findByUserIdIn(List<Long> userIds)",
    "findByIdIn(List<Long> var1)": "findByIdIn(List<Long> ids)",
    "findByBloodGroupAndCityAndIsAvailableTrue(BloodGroup var1, String var2)": "findByBloodGroupAndCityAndIsAvailableTrue(BloodGroup bloodGroup, String city)",
    "findByBloodGroupInAndCityIgnoreCaseAndIsAvailableTrue(List<BloodGroup> var1, String var2)": "findByBloodGroupInAndCityIgnoreCaseAndIsAvailableTrue(List<BloodGroup> bloodGroups, String city)",
    "findByBloodGroupInAndIsAvailableTrue(List<BloodGroup> var1)": "findByBloodGroupInAndIsAvailableTrue(List<BloodGroup> bloodGroups)",
    "countByBloodGroup(BloodGroup var1)": "countByBloodGroup(BloodGroup bloodGroup)",
    "countByBloodGroupAndIsAvailableTrue(BloodGroup var1)": "countByBloodGroupAndIsAvailableTrue(BloodGroup bloodGroup)",
    "calculateDistanceKm(@Param(value=\"lat1\") double var1, @Param(value=\"lng1\") double var3, @Param(value=\"lat2\") double var5, @Param(value=\"lng2\") double var7)": "calculateDistanceKm(@Param(value=\"lat1\") double lat1, @Param(value=\"lng1\") double lng1, @Param(value=\"lat2\") double lat2, @Param(value=\"lng2\") double lng2)",
    "findNearByCompatibleDonors(@Param(value=\"lat\") double var1, @Param(value=\"lng\") double var3, @Param(value=\"radiusKm\") double var5, @Param(value=\"bloodGroups\") List<String> var7, @Param(value=\"limit\") int var8)": "findNearByCompatibleDonors(@Param(value=\"lat\") double lat, @Param(value=\"lng\") double lng, @Param(value=\"radiusKm\") double radiusKm, @Param(value=\"bloodGroups\") List<String> bloodGroups, @Param(value=\"limit\") int limit)",
    "calculateResponseRate(@Param(value=\"donorId\") Long var1)": "calculateResponseRate(@Param(value=\"donorId\") Long donorId)"
}

def clean_file(path: str) -> bool:
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    original = content
    # Remove CFR header
    content = CFR_HEADER_PATTERN.sub("", content)
    # Remove empty catch block comments
    content = EMPTY_CATCH_PATTERN.sub("", content)

    # Replace interface parameter names
    for target, repl in INTERFACE_PARAM_REPLACEMENTS.items():
        if target in content:
            content = content.replace(target, repl)

    if content != original:
        with open(path, "w", encoding="utf-8") as f:
            f.write(content)
        return True
    return False

def main():
    cleaned_count = 0
    total_files = 0
    for root, _, files in os.walk(SRC_DIR):
        for file in files:
            if file.endswith(".java"):
                total_files += 1
                full_path = os.path.join(root, file)
                if clean_file(full_path):
                    cleaned_count += 1

    print(f"Total Java files scanned: {total_files}")
    print(f"Files cleaned of decompiler artifacts: {cleaned_count}")

if __name__ == "__main__":
    main()
