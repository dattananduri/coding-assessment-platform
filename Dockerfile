# ==========================================
# Stage 1: Build React 19 Frontend
# ==========================================
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# ==========================================
# Stage 2: Build Spring Boot Backend (Java 21)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS backend-builder
WORKDIR /app/backend

# Install Maven
RUN apk add --no-cache maven

COPY backend/pom.xml .
RUN mvn dependency:go-offline -B || true

COPY backend/src ./src

# Copy built frontend assets to Spring Boot static resources
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static/

RUN mvn clean package -DskipTests -B

# ==========================================
# Stage 3: Production Runtime
# ==========================================
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Ensure full JDK with javac is installed for candidate Java sandbox execution
RUN apk add --no-cache openjdk21-jdk curl bash

# Create data directories for SQLite/H2 and audio recordings
RUN mkdir -p /app/data/recordings /app/data/sandbox

# Copy executable jar
COPY --from=backend-builder /app/backend/target/coding-assessment-backend-1.0.0.jar app.jar

# Render automatically sets the PORT environment variable
ENV PORT=8080
EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:${PORT}/api/assessment/info/DEMO90 || exit 1

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
