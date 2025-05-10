# Stage 1: Build the application
FROM maven:3.9.2-amazoncorretto-17 AS builder
WORKDIR /app

# Copy the Maven wrapper and POM
COPY backend_web/VanEase/mvnw .
COPY backend_web/VanEase/.mvn ./.mvn
COPY backend_web/VanEase/pom.xml .

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (this will be cached if no changes)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY backend_web/VanEase/src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Stage 2: Create the runtime image
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Health check using the ping endpoint
HEALTHCHECK --interval=30s --timeout=30s --start-period=60s --retries=3 \
  CMD wget -q -O- http://localhost:8080/api/health/ping || exit 1

# Run the application with memory settings
ENTRYPOINT ["java", "-Xms128m", "-Xmx512m", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]
