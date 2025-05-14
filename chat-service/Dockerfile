# Etapa 1: Construcción
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final de ejecución
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiamos el jar y el entrypoint
COPY --from=build /app/target/chat-service.jar chat-service.jar
COPY entrypoint.sh /entrypoint.sh

# Damos permisos de ejecución
RUN chmod +x /entrypoint.sh

# Instalamos curl para el healthcheck
RUN apk add --no-cache curl

# Exponemos el puerto
EXPOSE 8083

# Usamos el entrypoint custom
ENTRYPOINT ["/entrypoint.sh"]