# Use Maven to build the application
FROM maven:3.9.2-amazoncorretto-17 AS build
WORKDIR /app

# Copy the entire repository
COPY . .

# Make mvnw executable
RUN chmod +x backend_web/VanEase/mvnw

# Build the application
RUN cd backend_web/VanEase && ./mvnw clean package -DskipTests

# Use a lightweight JDK image to run the app
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/backend_web/VanEase/target/VanEase-0.0.1-SNAPSHOT.jar app.jar

# Install wget for health checks
RUN apk add --no-cache wget

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Health check configuration
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/api/health/simple || exit 1

# Expose the port the app runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
