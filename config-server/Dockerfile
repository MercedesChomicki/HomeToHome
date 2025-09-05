# Etapa 0: Imagen con dependencias cacheadas
FROM maven:3.9.9-eclipse-temurin-21-alpine AS deps

WORKDIR /app

# Copiamos solo el pom.xml (esto permite que Maven descargue dependencias
# sin necesidad de copiar el código completo todavía)
COPY pom.xml .

# Descargar dependencias y guardarlas en /root/.m2
RUN mvn dependency:go-offline

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
COPY --from=build /app/target/config-server.jar config-server.jar
COPY entrypoint.sh /entrypoint.sh

# Damos permisos de ejecución
RUN chmod +x /entrypoint.sh

# Instalamos curl para el healthcheck
RUN apk add --no-cache curl

EXPOSE 8888

# Usamos el entrypoint custom
ENTRYPOINT ["/entrypoint.sh"]