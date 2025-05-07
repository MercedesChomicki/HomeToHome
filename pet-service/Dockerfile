# Etapa 1: Construcción
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final de ejecución
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiamos el jar y el entrypoint
COPY --from=build /app/target/pet-service.jar pet-service.jar
COPY entrypoint.sh /entrypoint.sh

# Damos permisos de ejecución
RUN chmod +x /entrypoint.sh

# Instalamos curl para el healthcheck
RUN apk add --no-cache curl

# Exponemos el puerto
EXPOSE 8082

# Usamos el entrypoint custom
ENTRYPOINT ["/entrypoint.sh"]