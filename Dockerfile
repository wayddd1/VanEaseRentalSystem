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

# Install wget and curl for health checks
RUN apk add --no-cache wget curl

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=railway
ENV SERVER_PORT=8080
ENV SPRING_MAIN_ALLOW_BEAN_DEFINITION_OVERRIDING=true
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

# Health check configuration - increased start period to 120s
HEALTHCHECK --interval=10s --timeout=5s --start-period=120s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

EXPOSE 8080

# Use shell form to allow environment variable expansion
ENTRYPOINT java $JAVA_OPTS -Dspring.profiles.active=railway -jar app.jar
