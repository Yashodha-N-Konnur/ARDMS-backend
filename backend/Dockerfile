# =====================================================
# ARDMS Backend - Multi-stage Docker Build
# Stage 1: Build with Maven
# Stage 2: Run with minimal JRE
# =====================================================

# ---- Build Stage ----
FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml first for better layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="ARDMS Team <devops@ardms.company.com>"
LABEL version="1.0.0"
LABEL description="Automated Release & Deployment Management System"

# Create non-root user for security
RUN addgroup -S ardms && adduser -S ardms -G ardms

WORKDIR /app

# Create directories
RUN mkdir -p /app/logs /app/config && \
    chown -R ardms:ardms /app

# Copy the JAR from builder stage
COPY --from=builder /build/target/ardms-backend-*.jar app.jar
RUN chown ardms:ardms app.jar

# Switch to non-root user
USER ardms

# JVM tuning flags
ENV JAVA_OPTS="-Xms512m -Xmx1024m \
    -XX:+UseG1GC \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=docker"

ENV LOG_PATH=/app/logs
ENV SERVER_PORT=8080

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
