# 1. Use official Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# 2. Set working directory
WORKDIR /app

# 3. Copy Maven wrapper and pom.xml (cache dependencies)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# 4. Download dependencies offline
RUN ./mvnw dependency:go-offline

# 5. Copy the entire source code
COPY src src

# 6. Build the application JAR (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# 7. Expose Spring Boot port
EXPOSE 8080

# 8. Run the built JAR dynamically
# Use wildcard to match the generated JAR name
ENTRYPOINT ["sh", "-c", "java -jar target/*.jar"]
