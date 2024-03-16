FROM maven:3.8.5-openjdk-17-slim as builder

WORKDIR '/app'

COPY . .

RUN ["mvn", "clean", "package"]

FROM openjdk:17-jdk-alpine

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app.jar"]
