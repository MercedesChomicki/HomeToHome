# Etapa 0: Imagen con dependencias cacheadas
FROM maven-deps-cache AS deps

# Etapa 1: Build del microservicio con Maven
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Reutilizar la caché de dependencias
COPY --from=deps /root/.m2 /root/.m2

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Imagen final de ejecución
FROM eclipse-temurin:21-jdk-alpine
# Agregar wget para healthcheck
RUN apk add --no-cache wget
WORKDIR /app
COPY --from=build /app/target/eureka-server.jar eureka-server.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]