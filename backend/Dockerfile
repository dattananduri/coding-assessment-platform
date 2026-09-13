# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Install maven
RUN apk add --no-cache maven && mvn clean package -DskipTests

# Stage 2: Production Runtime
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Install javac and standard tools for sandbox execution
RUN apk add --no-cache openjdk21-jdk
COPY --from=build /app/target/coding-assessment-backend-1.0.0.jar app.jar
RUN mkdir -p /app/data/recordings /app/data/sandbox
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
