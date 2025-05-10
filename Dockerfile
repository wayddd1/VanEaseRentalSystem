# Use Eclipse Temurin for Java 21 (as specified in pom.xml)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first for better layer caching
COPY backend_web/VanEase/pom.xml backend_web/VanEase/
COPY backend_web/VanEase/mvnw backend_web/VanEase/
COPY backend_web/VanEase/.mvn backend_web/VanEase/.mvn/

# Make mvnw executable
RUN chmod +x backend_web/VanEase/mvnw

# Download dependencies first (for better caching)
RUN cd backend_web/VanEase && ./mvnw dependency:go-offline -B

# Copy source code
COPY backend_web/VanEase/src backend_web/VanEase/src/

# Build the application
RUN cd backend_web/VanEase && ./mvnw clean package -DskipTests

# Use a lightweight JDK image for runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/backend_web/VanEase/target/VanEase-0.0.1-SNAPSHOT.jar app.jar

# Install wget for health checks
RUN apk add --no-cache wget curl

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Health check configuration - increased timeout and start period
HEALTHCHECK --interval=30s --timeout=30s --start-period=120s --retries=5 \
  CMD wget -q --spider http://localhost:8080/api/health/simple || exit 1

# Expose the port the app runs on
EXPOSE 8080

# Run the application with memory settings
CMD ["java", "-Xms128m", "-Xmx512m", "-jar", "app.jar"]
