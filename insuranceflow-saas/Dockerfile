# ========== BUILD STAGE ==========
FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# ========== PRODUCTION STAGE ==========
FROM eclipse-temurin:25-jdk-alpine

# Security: create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Security: copy only the jar
COPY --from=build /app/target/*.jar app.jar

# Security: set file ownership to non-root user
RUN chown -R appuser:appgroup /app

# Security: run as non-root user
USER appuser

EXPOSE 8080

# Security: JVM hardening flags
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT:-8080} -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar"]
