# Stage 1: Build the application
FROM openjdk:23-jdk-slim AS builder
WORKDIR /app

# Copy the project files
COPY . .

# Grant execute permissions to the Gradle wrapper script
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean build -x test

# Stage 2: Create the final image
FROM openjdk:23-jre-slim
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar ./app.jar

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]