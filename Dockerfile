FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY backend_web/VanEase/pom.xml .
# Download dependencies first for better caching
RUN mvn dependency:go-offline
COPY backend_web/VanEase/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/VanEase-0.0.1-SNAPSHOT.jar app.jar

# Install wget for health checks
RUN apk add --no-cache wget

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=railway

# Health check configuration
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/ || exit 1

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
