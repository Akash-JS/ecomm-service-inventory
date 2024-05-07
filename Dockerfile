# Use a base image with JDK 17
FROM openjdk:17-jdk-alpine AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the Maven configuration file
COPY pom.xml .

# Download the dependencies and plugins into a separate layer
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline dependency:resolve-plugins

# Copy the application source code
COPY src ./src

# Build the application
RUN --mount=type=cache,target=/root/.m2 mvn -o package -DskipTests

# Create a new stage for the final image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage to the final image
COPY --from=builder /app/target/ecomm-service-inventory-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port your application runs on
EXPOSE 8083

# Specify the command to run your application
CMD ["java", "-jar", "app.jar"]
