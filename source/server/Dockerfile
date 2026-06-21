# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
# Pre-download dependencies to cache layer
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# The application binds dynamically to $PORT, but defaults to 8888
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
