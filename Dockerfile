# Use an official Maven image to build the library
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set workdir
WORKDIR /app

# Copy pom and source files
COPY pom.xml .
COPY src ./src

# Download dependencies and build the jar
RUN mvn clean install -DskipTests

# Runtime image just to hold the jar (optional)
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /spring-boot-job-monitor

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar ./spring-boot-job-monitor.jar

# Optional entrypoint for test or debugging (most libraries don’t need this)
CMD ["java", "-jar", "spring-boot-job-monitor.jar"]
