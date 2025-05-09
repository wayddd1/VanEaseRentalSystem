FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY backend_web/VanEase/pom.xml .
COPY backend_web/VanEase/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/VanEase-0.0.1-SNAPSHOT.jar app.jar

# Install wget for health checks
RUN apk add --no-cache wget

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=railway
ENV SERVER_PORT=8080
ENV SPRING_MAIN_ALLOW_BEAN_DEFINITION_OVERRIDING=true

# Health check configuration
HEALTHCHECK --interval=5s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=railway", "-jar", "app.jar"]
