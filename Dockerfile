FROM maven:3.9-eclipse-temurin-24-alpine
WORKDIR /app
COPY . .
RUN mvn clean install -Dmaven.test.skip=true
CMD ["java", "-jar", "target/rinha-backend-2025-0.0.2-SNAPSHOT.jar"]