import yaml

with open('.github/workflows/ci.yml', 'r') as f:
    data = yaml.safe_load(f)

print("CI YAML IS 100% VALID!")
print("Workflow Name:", data.get("name"))
print("Triggers:", list(data.get("on", {}).keys()))
print("Jobs:", list(data.get("jobs", {}).keys()))
steps = data["jobs"]["build-and-test"]["steps"]
print(f"Total steps in build-and-test: {len(steps)}")
for i, step in enumerate(steps, 1):
    print(f"  Step {i}: {step.get('name')}")
