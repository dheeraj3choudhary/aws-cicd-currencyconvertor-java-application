# ---------------------------------------------------------------------------
# Dockerfile — Currency Converter Spring Boot App
# Multi-stage build:
#   Stage 1 (builder) — compiles the app with Maven
#   Stage 2 (runtime) — minimal JRE image, copies only the jar
# ---------------------------------------------------------------------------

# Stage 1 — Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies first (cached layer — only re-runs if pom.xml changes)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# Stage 2 — Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/currency-converter-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]