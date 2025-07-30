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
WORKDIR /app

COPY --from=build /app/target/chat-service.jar chat-service.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh # Damos permisos de ejecución
RUN apk add --no-cache curl # Instalamos curl para el healthcheck

EXPOSE 8083
ENTRYPOINT ["/entrypoint.sh"]