# Etapa 1: Construcción
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Imagen final de ejecución
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/pet-service.jar pet-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "pet-service.jar"]