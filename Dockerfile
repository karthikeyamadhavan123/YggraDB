# Step 1: Build stage
FROM maven:3.9.6-eclipse-temurin AS build

# Set working dir inside container
WORKDIR /app

# Copy pom.xml and download dependencies first (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build project (skip tests for speed, optional)
RUN mvn clean package -DskipTests

# Step 2: Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built jar from previous stage
COPY --from=build /app/target/*.jar app.jar

# Command to run YggraDB
ENTRYPOINT ["java", "-jar", "app.jar"]