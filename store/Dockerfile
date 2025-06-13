# Multi-stage build for store service
FROM gradle:8.13-jdk21 AS builder

WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ ./gradle/

# Copy common module first (for better caching)
COPY common/ ./common/

# Copy store service source
COPY store/ ./store/

# Build the application
RUN ./gradlew store:build -x test --no-daemon

# Runtime stage
FROM openjdk:21-jdk-slim

# Create app user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Install necessary packages for external API calls
RUN apt-get update && apt-get install -y \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/* \
    && update-ca-certificates

# Create app directory
RUN mkdir -p /app && \
    chown -R appuser:appgroup /app

WORKDIR /app

# Copy jar file from builder stage
COPY --from=builder /app/store/build/libs/store-*.jar app.jar

# Change ownership
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"
ENV SPRING_PROFILES_ACTIVE=docker

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]