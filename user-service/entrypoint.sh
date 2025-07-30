#!/bin/sh
set -e

echo "Esperando a Eureka en ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE}/apps..."
until curl -s http://eureka-server:8761/eureka/apps > /dev/null; do
  echo "Eureka aún no está disponible. Esperando 10 segundos..."
  sleep 10
done

echo "Esperando a Config Server en ${SPRING_CLOUD_CONFIG_URI}/actuator/health..."
until curl -s "${SPRING_CLOUD_CONFIG_URI}/actuator/health" | grep UP > /dev/null; do
  echo "Config Server aún no está disponible. Esperando 10 segundos..."
  sleep 10
done

echo "Esperando a la base de datos user-postgres:5432..."
until nc -z user-postgres 5432; do
  echo "user-postgres aún no está disponible. Esperando 5 segundos..."
  sleep 5
done

echo "Todos los servicios están disponibles. Iniciando user-service..."
exec java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar user-service.jar
