#!/usr/bin/env python3
"""
Full-Stack API Contract Checker
Matches Frontend Axios calls (method + path) against Backend Spring Boot Controller endpoints.
"""

import os
import re
import sys
from pathlib import Path

# Paths relative to backend root
BACKEND_SRC = Path(__file__).resolve().parent.parent / "src" / "main" / "java"
FRONTEND_SRC = Path(__file__).resolve().parent.parent.parent.parent / "Frontend" / "ai-blood-donor-ui" / "src"

MAPPING_ANNOTATIONS = {
    "GetMapping": "GET",
    "PostMapping": "POST",
    "PutMapping": "PUT",
    "DeleteMapping": "DELETE",
    "PatchMapping": "PATCH",
    "RequestMapping": "ALL",
}

FRONTEND_BASE_CONSTANTS = {
    "${ADMIN_BASE_URL}": "/admin",
    "${AUTH_BASE_URL}": "/auth",
    "${DONOR_BASE_URL}": "/donor",
    "${STATS_BASE_URL}": "/stats",
    "${CONFIG.API.BASE_URL}": "",
}

def clean_path(path: str) -> str:
    """Normalize path by stripping query params, leading/trailing slashes, and unifying param tokens."""
    if not path:
        return "/"
    path = path.split("?")[0].strip()
    
    # Resolve known frontend constant prefixes
    for const_key, const_val in FRONTEND_BASE_CONSTANTS.items():
        if const_key in path:
            path = path.replace(const_key, const_val)

    if not path.startswith("/"):
        path = "/" + path
    if not path.startswith("/api"):
        path = "/api" + path

    # Clean double slashes
    path = re.sub(r"/+", "/", path)
    if path.endswith("/") and len(path) > 1:
        path = path[:-1]

    # Normalize all variable path segments to {param}
    # Handles ${requireId(userId, 'userId')}, {id}, ${id}, ${normalizedDonorId}, etc.
    path = re.sub(r"\$\{requireId\([^)]+\)\}", "{param}", path)
    path = re.sub(r"\{[^\}]+\}", "{param}", path)
    path = re.sub(r"\$\{[^\}]+\}", "{param}", path)
    path = re.sub(r"/:[a-zA-Z0-9_]+", "/{param}", path)
    return path

def extract_backend_endpoints():
    backend_endpoints = []
    
    for java_file in BACKEND_SRC.rglob("*.java"):
        content = java_file.read_text(encoding="utf-8", errors="ignore")
        if "@RestController" not in content:
            continue

        # Find class-level @RequestMapping
        class_prefix = ""
        class_match = re.search(r'@(?:org\.springframework\.web\.bind\.annotation\.)?RequestMapping\s*\(\s*(?:(?:value|path)\s*=\s*)?\{?\s*["\']([^"\']+)["\']', content)
        if class_match:
            class_prefix = class_match.group(1).strip()
            if not class_prefix.startswith("/"):
                class_prefix = "/" + class_prefix

        # Regex for method mappings matching value/path with optional package prefix & braces {}
        method_pattern = re.compile(
            r'@(?:org\.springframework\.web\.bind\.annotation\.)?(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping|RequestMapping)\s*'
            r'(?:\(\s*(?:(?:value|path)\s*=\s*)?\{?\s*(?:["\']([^"\']*)["\'])?)?',
            re.MULTILINE
        )

        for match in method_pattern.finditer(content):
            ann = match.group(1)
            method = MAPPING_ANNOTATIONS.get(ann, "GET")
            path_val = match.group(2) or ""

            # Check if RequestMapping has explicit HTTP method
            full_header = content[match.start():match.end() + 80]
            if ann == "RequestMapping":
                rm_method = re.search(r'method\s*=\s*\{?\s*RequestMethod\.([A-Z]+)', full_header)
                if rm_method:
                    method = rm_method.group(1)
                else:
                    method = "ALL"

            combined = class_prefix + (("/" + path_val.lstrip("/")) if path_val else "")
            cleaned = clean_path(combined)

            backend_endpoints.append({
                "method": method,
                "path": cleaned,
                "raw_path": combined,
                "file": java_file.name
            })

    return backend_endpoints

def extract_frontend_calls():
    frontend_calls = []
    
    # Matches axiosInstance.<method>(`path`) or axiosInstance.<method>('path') or axiosInstance.<method>("path")
    # Captures balanced template strings with backticks
    template_pattern = re.compile(
        r'(?:axiosInstance|axios|request)\.(get|post|put|delete|patch)\s*\(\s*`([^`\n]+)`',
        re.MULTILINE
    )
    quote_pattern = re.compile(
        r'(?:axiosInstance|axios|request)\.(get|post|put|delete|patch)\s*\(\s*["\']([^"\'\n]+)["\']',
        re.MULTILINE
    )

    for ts_file in FRONTEND_SRC.rglob("*.ts*"):
        if "node_modules" in str(ts_file) or "test" in ts_file.name or ".d.ts" in ts_file.name:
            continue
        content = ts_file.read_text(encoding="utf-8", errors="ignore")

        for match in template_pattern.finditer(content):
            method = match.group(1).upper()
            raw_path = match.group(2).strip()
            cleaned = clean_path(raw_path)
            frontend_calls.append({
                "method": method,
                "path": cleaned,
                "raw_path": raw_path,
                "file": ts_file.name,
                "line": content[:match.start()].count("\n") + 1
            })

        for match in quote_pattern.finditer(content):
            method = match.group(1).upper()
            raw_path = match.group(2).strip()
            cleaned = clean_path(raw_path)
            frontend_calls.append({
                "method": method,
                "path": cleaned,
                "raw_path": raw_path,
                "file": ts_file.name,
                "line": content[:match.start()].count("\n") + 1
            })

    return frontend_calls

def main():
    print("==================================================================")
    print("FULL-STACK API CONTRACT & ENDPOINT VERIFICATION")
    print("==================================================================")
    
    backend_eps = extract_backend_endpoints()
    frontend_calls = extract_frontend_calls()

    print(f"Backend Controller Endpoints Discovered: {len(backend_eps)}")
    print(f"Frontend Axios Calls Discovered:        {len(frontend_calls)}")
    print("------------------------------------------------------------------")

    matched = []
    mismatched = []

    for fc in frontend_calls:
        match_found = False
        for be in backend_eps:
            method_matches = (be["method"] == "ALL" or be["method"] == fc["method"])
            path_matches = (be["path"] == fc["path"])
            if method_matches and path_matches:
                matched.append((fc, be))
                match_found = True
                break

        if not match_found:
            mismatched.append(fc)

    # Deduplicate matched for summary display
    unique_matched = []
    seen = set()
    for fc, be in matched:
        key = (fc["method"], fc["path"])
        if key not in seen:
            seen.add(key)
            unique_matched.append((fc, be))

    print(f"\n[+] Verified Matching Endpoints ({len(unique_matched)} unique contracts):")
    for fc, be in sorted(unique_matched, key=lambda x: (x[0]["method"], x[0]["path"])):
        print(f"  ✓ [{fc['method']}] {fc['path']}  -->  Backend: {be['file']}")

    if mismatched:
        print(f"\n[-] Potential Contract Discrepancies / Missing Endpoints ({len(mismatched)}):")
        # Deduplicate mismatched
        seen_m = set()
        for m in mismatched:
            key = (m["method"], m["path"])
            if key not in seen_m:
                seen_m.add(key)
                print(f"  ✗ [{m['method']}] {m['raw_path']} (in {m['file']}:{m['line']})")
                print(f"      Resolved: {m['path']}  [NO MATCHING CONTROLLER METHOD]")
    else:
        print("\n[+] 100% Contract Match! Zero Mismatches between Frontend Axios Calls and Backend Controllers.")

    print("\n==================================================================")
    return 0 if len(mismatched) == 0 else 1

if __name__ == "__main__":
    sys.exit(main())
