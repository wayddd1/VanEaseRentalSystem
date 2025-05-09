FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY backend_web/VanEase/pom.xml .
COPY backend_web/VanEase/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/VanEase-0.0.1-SNAPSHOT.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=railway
ENV SERVER_PORT=8080
ENV SPRING_MAIN_ALLOW_BEAN_DEFINITION_OVERRIDING=true

# Health check configuration
HEALTHCHECK --interval=5s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
