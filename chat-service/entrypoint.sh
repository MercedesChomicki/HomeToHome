#!/bin/sh
set -e

echo "Esperando a Eureka en http://eureka-server:8761/eureka/apps..."
until curl -s http://eureka-server:8761/eureka/apps > /dev/null; do
  echo "Eureka aún no está disponible. Esperando 10 segundos..."
  sleep 10
done

echo "Esperando a Config Server en ${SPRING_CLOUD_CONFIG_URI}/actuator/health..."
until curl -s "${SPRING_CLOUD_CONFIG_URI}/actuator/health" | grep UP > /dev/null; do
  echo "Config Server aún no está disponible. Esperando 10 segundos..."
  sleep 10
done

echo "Esperando a la base de datos mongo-db:27017..."
until nc -z mongo-db 27017; do
  echo "mongo-db aún no está disponible. Esperando 5 segundos..."
  sleep 5
done

echo "Todos los servicios están disponibles. Iniciando chat-service..."
exec java -jar chat-service.jar
