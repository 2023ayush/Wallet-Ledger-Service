# 1. Use official Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# 2. Set working directory inside container
WORKDIR /app

# 3. Copy Maven wrapper and pom.xml first (for caching)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# 4. Download dependencies
RUN ./mvnw dependency:go-offline

# 5. Copy source code
COPY src src

# 6. Build the application
RUN ./mvnw clean package -DskipTests

# 7. Expose Spring Boot port
EXPOSE 8080

# 8. Run the application
ENTRYPOINT ["java", "-jar", "target/wallet-ledger-service.jar"]
