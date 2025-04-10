FROM eclipse-temurin:22-jdk
WORKDIR /app
COPY backend/ /app
RUN ./mvnw clean package -DskipTests
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
