# Build stage
FROM maven:3.9.2-amazoncorretto-17 AS build
WORKDIR /app

# Copy the project files
COPY backend_web/VanEase/pom.xml .
COPY backend_web/VanEase/src ./src/

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Install curl for health check
RUN apk add --no-cache curl

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/health/ping || exit 1

# Run the application
ENTRYPOINT ["java", "-Xms128m", "-Xmx512m", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]
