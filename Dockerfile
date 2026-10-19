# ══════════════════════════════════════════════════════════════
# MULTI-STAGE DOCKERFILE FOR AI BLOOD DONOR NETWORK BACKEND
# ══════════════════════════════════════════════════════════════

# ── Stage 1: Build Application ─────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /workspace

# Cache dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compile and package application jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Hardened Production Runtime ───────────────────────
FROM eclipse-temurin:21-jre-jammy AS runner

# Create dedicated non-root user and group (Principle of Least Privilege)
RUN groupadd -r -g 10001 appgroup && \
    useradd -r -u 10001 -g appgroup -m -d /app -s /sbin/nologin appuser

WORKDIR /app
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

# Install curl for container health check
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copy application jar from builder stage
COPY --from=builder --chown=appuser:appgroup /workspace/target/*.jar app.jar

# Run as non-root user
USER appuser

# Container-aware JVM optimization flags tuned for 512MB RAM constraints (Render free tier)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=55.0 -XX:InitialRAMPercentage=40.0 -Xss512k -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:+ExitOnOutOfMemoryError -Duser.timezone=UTC"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

# Native Docker Healthcheck targeting public Actuator health probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -fsS http://localhost:${PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
