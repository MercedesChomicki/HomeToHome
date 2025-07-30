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

# Copiamos el jar y el entrypoint
COPY --from=build /app/target/user-service.jar user-service.jar
COPY entrypoint.sh /entrypoint.sh

# Damos permisos de ejecución
RUN chmod +x /entrypoint.sh

# Instalamos curl para el healthcheck
RUN apk add --no-cache curl

# Exponemos el puerto
EXPOSE 8081

# Usamos el entrypoint custom
ENTRYPOINT ["/entrypoint.sh"]