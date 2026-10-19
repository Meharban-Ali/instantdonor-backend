#!/usr/bin/env python3
"""
Environment Variables Reconciliation Script (READ-ONLY)
Scans codebase for environment variable references and reconciles them against
render.yaml, docs/DEPLOY.md, and .env.example.
"""
import os
import re
import sys
from pathlib import Path

def get_repo_root():
    current = Path(__file__).resolve().parent
    while current != current.parent:
        if (current / ".git").exists() or (current / "pom.xml").exists() or (current / "package.json").exists():
            return current
        current = current.parent
    return Path(__file__).resolve().parent.parent

def scan_backend_env_vars(repo_root):
    env_vars = {} # name -> { 'code': set(), 'default': None, 'prod_required': False }
    
    # 1. Scan application*.properties
    prop_files = list(repo_root.glob("src/main/resources/application*.properties"))
    prop_pattern = re.compile(r'\$\{([A-Za-z0-9_]+)(?::([^}]*))?\}')
    
    for pf in prop_files:
        is_prod = "prod" in pf.name
        with open(pf, "r", encoding="utf-8", errors="ignore") as f:
            for line_no, line in enumerate(f, 1):
                line = line.strip()
                if line.startswith("#") or not line:
                    continue
                for match in prop_pattern.finditer(line):
                    var_name = match.group(1)
                    default_val = match.group(2)
                    if var_name not in env_vars:
                        env_vars[var_name] = {
                            'code': set(),
                            'default': default_val,
                            'prod_required': False
                        }
                    rel_path = pf.relative_to(repo_root)
                    env_vars[var_name]['code'].add(f"{rel_path}:{line_no}")
                    if default_val is not None and env_vars[var_name]['default'] is None:
                        env_vars[var_name]['default'] = default_val
                    if is_prod and (default_val is None or default_val == ""):
                        env_vars[var_name]['prod_required'] = True

    # 2. Scan Java files for System.getenv
    java_files = list(repo_root.glob("src/main/java/**/*.java"))
    getenv_pattern = re.compile(r'System\.getenv\(\s*["\']([A-Za-z0-9_]+)["\']\s*\)')
    for jf in java_files:
        with open(jf, "r", encoding="utf-8", errors="ignore") as f:
            for line_no, line in enumerate(f, 1):
                for match in getenv_pattern.finditer(line):
                    var_name = match.group(1)
                    if var_name not in env_vars:
                        env_vars[var_name] = {
                            'code': set(),
                            'default': None,
                            'prod_required': False
                        }
                    rel_path = jf.relative_to(repo_root)
                    env_vars[var_name]['code'].add(f"{rel_path}:{line_no}")

    # ProductionStartupValidator checks
    prod_validator = repo_root / "src/main/java/com/blood_donor/config/ProductionStartupValidator.java"
    if prod_validator.exists():
        with open(prod_validator, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()
            for check in ["DB_URL", "DB_USERNAME", "DB_PASSWORD", "REDIS_HOST", "REDIS_PORT", "AI_SERVICE_API_KEY", "JWT_SECRET"]:
                if check in env_vars:
                    env_vars[check]['prod_required'] = True

    return env_vars

def parse_render_yaml(yaml_path):
    keys = set()
    if not yaml_path.exists():
        return keys
    with open(yaml_path, "r", encoding="utf-8", errors="ignore") as f:
        for line in f:
            line = line.strip()
            if line.startswith("- key:"):
                key = line.split(":", 1)[1].strip()
                keys.add(key)
    return keys

def parse_env_example(env_path):
    keys = set()
    if not env_path.exists():
        return keys
    with open(env_path, "r", encoding="utf-8", errors="ignore") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            if "=" in line:
                key = line.split("=", 1)[0].strip()
                keys.add(key)
    return keys

def parse_deploy_md(md_path):
    keys = set()
    if not md_path.exists():
        return keys
    with open(md_path, "r", encoding="utf-8", errors="ignore") as f:
        for line in f:
            for match in re.finditer(r'`([A-Z0-9_]{3,})`', line):
                keys.add(match.group(1))
    return keys

def main():
    root = get_repo_root()
    print(f"Scanning Repository: {root.name} ({root})")

    env_vars = scan_backend_env_vars(root)
    render_yaml_keys = parse_render_yaml(root / "render.yaml")
    env_example_keys = parse_env_example(root / ".env.example")
    deploy_md_keys = parse_deploy_md(root / "docs/DEPLOY.md")

    # Combine all known keys
    all_keys = sorted(set(env_vars.keys()) | render_yaml_keys | env_example_keys)

    headers = ["Variable", "In Code", "Default", "Prod Required", "render.yaml", "DEPLOY.md", ".env.example"]
    rows = []

    for k in all_keys:
        in_code = "Yes" if k in env_vars else "No"
        default = env_vars[k]['default'] if k in env_vars and env_vars[k]['default'] is not None else "-"
        prod_req = "YES" if k in env_vars and env_vars[k]['prod_required'] else "No"
        in_render = "Yes" if k in render_yaml_keys else "NO"
        in_deploy = "Yes" if k in deploy_md_keys else "NO"
        in_example = "Yes" if k in env_example_keys else "NO"
        rows.append([k, in_code, str(default), prod_req, in_render, in_deploy, in_example])

    col_widths = [max(len(row[i]) for row in ([headers] + rows)) for i in range(len(headers))]
    
    def format_row(row):
        return "| " + " | ".join(row[i].ljust(col_widths[i]) for i in range(len(row))) + " |"

    separator = "|-" + "-|-".join("-" * w for w in col_widths) + "-|"

    print("\n" + format_row(headers))
    print(separator)
    for r in rows:
        print(format_row(r))

if __name__ == "__main__":
    main()
