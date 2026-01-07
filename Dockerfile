# Use official OpenJDK image as base
FROM eclipse-temurin:21-jdk-jammy
# Set working directory inside container
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this layer will be cached)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port 8000
EXPOSE 8000

# Run the application
ENTRYPOINT ["java", "-jar", "target/e-commerce-0.0.1-SNAPSHOT.jar"]