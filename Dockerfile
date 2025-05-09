FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY backend_web/VanEase/pom.xml .
COPY backend_web/VanEase/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/VanEase-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=railway
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
